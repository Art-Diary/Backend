package klieme.artdiary.fcm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.push_alarm.PushAlarm;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.favoriteexh.data_access.repository.FavoriteExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendAlarmService {

	private final FavoriteExhRepository favoriteExhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final PushAlarm pushAlarm;

	@Autowired
	public SendAlarmService(FavoriteExhRepository favoriteExhRepository, ExhVisitRepository exhVisitRepository,
		PushAlarm pushAlarm) {
		this.favoriteExhRepository = favoriteExhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.pushAlarm = pushAlarm;
	}

	public void sendMessageAboutExh() {
		log.info("[알림 보내기]");
		List<FcmSendDto> fcmSendDtoList = new ArrayList<>();
		// 좋아요한 전시회 시작일/마감일 알림
		aboutFavorite(fcmSendDtoList);
		// 혼자 가는 전시회 날짜 알림
		aboutVisitSoloDate(fcmSendDtoList);
		// 모임에서 가는 전시회 날짜 알림
		aboutVisitGatheringDate(fcmSendDtoList);
		// push
		for (FcmSendDto fcmSendDto : fcmSendDtoList) {
			try {
				System.out.println(fcmSendDto.getBody() + " " + fcmSendDto.getToken());
				pushAlarm.sendMessageTo(fcmSendDto);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void aboutFavorite(List<FcmSendDto> fcmSendDtoList) {
		// 좋아요한 전시회 시작일/마감일 알림
		List<Map<String, Object>> favoriteInfoList = favoriteExhRepository.getFavoriteExhWithUserAndExh();
		LocalDate localDate = LocalDate.now();

		for (Map<String, Object> info : favoriteInfoList) {
			UserEntity userEntity = (UserEntity)info.get("user");
			ExhEntity exhEntity = (ExhEntity)info.get("exhibition");
			String title = null;
			String body = null;

			if (localDate.isEqual(exhEntity.getExhPeriodStart())) {
				title = "오늘은 좋아요한 전시회 시작일";
				body = "\"" + exhEntity.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!";
			} else if (localDate.isEqual(exhEntity.getExhPeriodEnd())) {
				title = "오늘은 좋아요한 전시회 종료일";
				body = "\"" + exhEntity.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!";
			}
			if (title != null) {
				fcmSendDtoList.add(FcmSendDto.builder()
					.token(userEntity.getAlarmToken())
					.title(title)
					.body(body)
					.type("exhibition")
					.exhId(exhEntity.getExhId())
					.build());
			}
		}
	}

	private void aboutVisitSoloDate(List<FcmSendDto> fcmSendDtoList) {
		// 혼자 가는 전시회 날짜 알림
		List<Map<String, Object>> visitInfoList = exhVisitRepository.getVisitSoloDateForFcm();

		for (Map<String, Object> visitInfo : visitInfoList) {
			UserEntity user = (UserEntity)visitInfo.get("user");
			ExhEntity exh = (ExhEntity)visitInfo.get("exhibition");

			fcmSendDtoList.add(FcmSendDto.builder()
				.token(user.getAlarmToken())
				.title("오늘은 혼자 전시회 가는 날")
				.body("\"" + exh.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!")
				.type("exhibition")
				.exhId(exh.getExhId())
				.build());
		}
	}

	private void aboutVisitGatheringDate(List<FcmSendDto> fcmSendDtoList) {
		// 모임에서 가는 전시회 날짜 알림
		List<Map<String, Object>> visitInfoList = exhVisitRepository.getVisitGatheringDateForFcm();

		for (Map<String, Object> visitInfo : visitInfoList) {
			UserEntity user = (UserEntity)visitInfo.get("user");
			ExhEntity exh = (ExhEntity)visitInfo.get("exhibition");
			GatheringEntity gathering = (GatheringEntity)visitInfo.get("gathering");

			fcmSendDtoList.add(FcmSendDto.builder()
				.token(user.getAlarmToken())
				.title("오늘은 \"" + gathering.getGatherName() + "\"이랑 전시회 가는 날")
				.body("\"" + exh.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!")
				.type("exhibition")
				.exhId(exh.getExhId())
				.build());
		}
	}
}
