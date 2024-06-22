package klieme.artdiary.fcm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.favoriteexhs.data_access.repository.FavoriteExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.myexhs.data_access.entity.UserExhEntity;
import klieme.artdiary.myexhs.data_access.repository.UserExhRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;

@Service
public class SendAlarmService {

	private final FavoriteExhRepository favoriteExhRepository;
	private final UserExhRepository userExhRepository;
	private final GatheringExhRepository gatheringExhRepository;
	@Value("${fcm.api.url}")
	private String FCM_API_URL;
	@Value("${firebase.config.path}")
	private String FIREBASE_CONFIG_PATH;

	@Autowired
	public SendAlarmService(FavoriteExhRepository favoriteExhRepository, UserExhRepository userExhRepository,
		GatheringExhRepository gatheringExhRepository) {
		this.favoriteExhRepository = favoriteExhRepository;
		this.userExhRepository = userExhRepository;
		this.gatheringExhRepository = gatheringExhRepository;
	}

	/*
	 * 좋아요 누른 전시회의 시작일과 종료일 알림
	 * - 시작날인 전시회에 대해서 알림
	 * - 종료일인 전시회에 대해서 알림
	 * 캘린더에 저장한 전시회 방문 날짜 알림
	 * - 캘린더에 저장한 전시회? -> 방문 날짜에 맞춰서 알림?
	 * */
	public void sendMessageAboutExh() throws IOException {
		List<FcmSendDto> fcmSendDtoList = new ArrayList<>();
		// 좋아요 누른 전시회의 시작일과 종료일 알림
		aboutFavorite(fcmSendDtoList);
		// 캘린더에 저장한 전시회 방문 날짜 알림
		aboutCalendar(fcmSendDtoList);
		// push
		for (FcmSendDto fcmSendDto : fcmSendDtoList) {
			sendMessageTo(fcmSendDto);
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
		List<Tuple> soloVisitInfo = userExhRepository.getVisitExhWithUser();
		List<Tuple> gatherVisitInfo = gatheringExhRepository.getVisitExhWithUser();
		LocalDate localDate = LocalDate.now();

		for (Tuple info : soloVisitInfo) {
			UserEntity userEntity = info.get(0, UserEntity.class);
			ExhEntity exhEntity = info.get(1, ExhEntity.class);
			UserExhEntity userExhEntity = info.get(2, UserExhEntity.class);
			// 전시회 날짜 비교
			if (userExhEntity != null && exhEntity != null
				&& userEntity != null && userExhEntity.getVisitDate() != null
				&& userEntity.getAlarmToken() != null) {
				if (userEntity.getAlarm3() && localDate.isEqual(userExhEntity.getVisitDate())) {
					fcmSendDtoList.add(FcmSendDto.builder()
						.token(userEntity.getAlarmToken())
						.title("캘린더에 저장한 전시회 방문 안내")
						.body("\"" + exhEntity.getExhName() + "\"" + " 오늘 방문 예정!")
						.exhId(exhEntity.getExhId())
						.build());
				}
			}
		}
		for (Tuple info : gatherVisitInfo) {
			UserEntity userEntity = info.get(0, UserEntity.class);
			ExhEntity exhEntity = info.get(1, ExhEntity.class);
			GatheringExhEntity gatheringExh = info.get(2, GatheringExhEntity.class);
			// 전시회 날짜 비교
			if (gatheringExh != null && exhEntity != null
				&& userEntity != null && gatheringExh.getVisitDate() != null
				&& userEntity.getAlarmToken() != null) {
				if (userEntity.getAlarm3() && localDate.isEqual(gatheringExh.getVisitDate())) {
					fcmSendDtoList.add(FcmSendDto.builder()
						.token(userEntity.getAlarmToken())
						.title("캘린더에 저장한 전시회 방문 안내")
						.body(exhEntity.getExhName() + " 오늘 방문 예정!")
						.exhId(exhEntity.getExhId())
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
		ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

		// System.out.println(response.getStatusCode());
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
