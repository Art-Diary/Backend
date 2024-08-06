package klieme.artdiary.fcm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.querydsl.core.Tuple;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.favoriteexh.data_access.repository.FavoriteExhRepository;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendAlarmService {

	private final FavoriteExhRepository favoriteExhRepository;
	private final ExhVisitRepository exhVisitRepository;
	@Value("${fcm.api.url}")
	private String FCM_API_URL;
	@Value("${firebase.config.path}")
	private String FIREBASE_CONFIG_PATH;

	@Autowired
	public SendAlarmService(FavoriteExhRepository favoriteExhRepository, ExhVisitRepository exhVisitRepository) {
		this.favoriteExhRepository = favoriteExhRepository;
		this.exhVisitRepository = exhVisitRepository;
	}

	/*
	 * 좋아요 누른 전시회의 시작일과 종료일 알림
	 * - 시작날인 전시회에 대해서 알림
	 * - 종료일인 전시회에 대해서 알림
	 * 캘린더에 저장한 전시회 방문 날짜 알림
	 * - 캘린더에 저장한 전시회? -> 방문 날짜에 맞춰서 알림?
	 * */
	public void sendMessageAboutExh() {
		log.info("[알림 보내기]");
		List<FcmSendDto> fcmSendDtoList = new ArrayList<>();
		// 좋아요 누른 전시회의 시작일과 종료일 알림
		aboutFavorite(fcmSendDtoList);
		// 캘린더에 저장한 전시회 방문 날짜 알림
		aboutCalendar(fcmSendDtoList);
		// push
		for (FcmSendDto fcmSendDto : fcmSendDtoList) {
			try {
				System.out.println(fcmSendDto.getBody() + " " + fcmSendDto.getToken());
				sendMessageTo(fcmSendDto);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void aboutFavorite(List<FcmSendDto> fcmSendDtoList) {
		// 좋아요 누른 전시회의 시작일과 종료일 알림
		List<Tuple> favoriteInfoList = favoriteExhRepository.getFavoriteExhWithUserAndExh();
		LocalDate localDate = LocalDate.now();

		for (Tuple info : favoriteInfoList) {
			UserEntity userEntity = info.get(0, UserEntity.class);
			ExhEntity exhEntity = info.get(1, ExhEntity.class);
			// 전시회 날짜 비교
			if (exhEntity != null && userEntity != null
				&& userEntity.getAlarmToken() != null) {
				String title = null;
				String body = null;
				if (userEntity.getAlarm1() && localDate.isEqual(exhEntity.getExhPeriodStart())) {
					title = "좋아요 전시회 시작일 안내";
					body = "\"" + exhEntity.getExhName() + "\"" + " 오늘 오픈!";
				} else if (userEntity.getAlarm2() && localDate.isEqual(exhEntity.getExhPeriodEnd())) {
					title = "좋아요 전시회 종료일 안내";
					body = "\"" + exhEntity.getExhName() + "\"" + " 오늘 종료!";
				}
				if (title != null) {
					fcmSendDtoList.add(FcmSendDto.builder()
						.token(userEntity.getAlarmToken())
						.title(title)
						.body(body)
						.exhId(exhEntity.getExhId())
						.build());
				}
			}
		}
	}

	private void aboutCalendar(List<FcmSendDto> fcmSendDtoList) {
		// 캘린더에 저장한 전시회 방문 날짜 알림
		List<Map<String, Object>> visitInfoList = exhVisitRepository.getVisitExhForFcm();
		LocalDate localDate = LocalDate.now();

		for (Map<String, Object> visitInfo : visitInfoList) {
			ExhVisitEntity exhVisit = (ExhVisitEntity)visitInfo.get("exhVisit");
			UserEntity user = (UserEntity)visitInfo.get("user");
			ExhEntity exh = (ExhEntity)visitInfo.get("exhibition");

			// 전시회 날짜 비교
			if (exhVisit != null && exh != null
				&& user != null && exhVisit.getVisitDate() != null
				&& user.getAlarmToken() != null) {
				if (user.getAlarm3() && localDate.isEqual(exhVisit.getVisitDate())) {
					fcmSendDtoList.add(FcmSendDto.builder()
						.token(user.getAlarmToken())
						.title("캘린더에 저장한 전시회 방문 안내")
						.body("오늘은 \"" + exh.getExhName() + "\"" + " 방문 예정일!")
						.exhId(exh.getExhId())
						.build());
				}
			}
		}
	}

	private void sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
		String message = makeMessage(fcmSendDto);
		RestTemplate restTemplate = new RestTemplate();

		restTemplate.getMessageConverters()
			.addFirst(new StringHttpMessageConverter(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + getAccessToken());

		HttpEntity<String> entity = new HttpEntity<>(message, headers);

		String API_URL = FCM_API_URL;
		try {
			ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			System.out.println("Wrong Token");
		}
	}

	/**
	 * Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받습니다.
	 *
	 * @return Bearer token
	 */
	private String getAccessToken() throws IOException {
		String firebaseConfigPath = FIREBASE_CONFIG_PATH;

		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		googleCredentials.refreshIfExpired();
		return googleCredentials.getAccessToken().getTokenValue();
	}

	/**
	 * FCM 전송 정보를 기반으로 메시지를 구성합니다. (Object -> String)
	 *
	 * @param fcmSendDto FcmSendDto
	 * @return String
	 */
	private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {

		ObjectMapper om = new ObjectMapper();
		FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
			.message(FcmMessageDto.Message.builder()
				.token(fcmSendDto.getToken())
				.data(FcmMessageDto.Notification.builder()
					.title(fcmSendDto.getTitle() + "/" + fcmSendDto.getExhId().toString())
					.body(fcmSendDto.getBody())
					.image(null)
					.build()
				).build()).validateOnly(false).build();

		return om.writeValueAsString(fcmMessageDto);
	}
}
