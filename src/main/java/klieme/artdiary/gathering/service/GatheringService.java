package klieme.artdiary.gathering.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.push_alarm.PushAlarm;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.fcm.FcmSendDto;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMateId;
import klieme.artdiary.gathering.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.gathering.info.ExhibitionInfo;
import klieme.artdiary.gathering.info.MateInfo;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class GatheringService implements GatheringOperationUseCase, GatheringReadUseCase {
	private final GatheringRepository gatheringRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final ExhRepository exhRepository;
	private final UserRepository userRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final PushAlarm pushAlarm;

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, GatheringMateRepository gatheringMateRepository,
		ExhRepository exhRepository, UserRepository userRepository, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository, PushAlarm pushAlarm) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.exhRepository = exhRepository;
		this.userRepository = userRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.pushAlarm = pushAlarm;
	}

	@Transactional
	@Override
	public GatheringReadUseCase.FindGatheringResult createGathering(GatheringCreateCommand command) {
		// 모임 생성
		GatheringEntity gatheringEntity = GatheringEntity.builder()
			.gatherName(command.getGatherName())
			.build();
		gatheringRepository.save(gatheringEntity);
		// 모임에 유저 추가
		GatheringMateEntity mateEntity = GatheringMateEntity.builder()
			.gatheringMateId(GatheringMateId.builder()
				.gatherId(gatheringEntity.getGatherId())
				.userId(getUserId())
				.build())
			.build();
		gatheringMateRepository.save(mateEntity);
		// 반환
		return GatheringReadUseCase.FindGatheringResult.findByGathering(gatheringEntity);
	}

	@Override
	public List<GatheringReadUseCase.FindGatheringResult> getGatheringList() {
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<GatheringReadUseCase.FindGatheringResult> gatherings = new ArrayList<>();

		// 사용자가 모임에서 최근에 전시회를 방문한 날짜 순으로 정렬됨.
		List<GatheringEntity> gatheringEntityList = gatheringMateRepository.getGatheringListByRecentVisitDate(userId);

		for (GatheringEntity gathering : gatheringEntityList) {
			gatherings.add(GatheringReadUseCase.FindGatheringResult.findByGathering(gathering));
		}
		return gatherings;
	}

	@Transactional
	@Override
	public List<FindGatheringExhResult> addExhAboutGathering(ExhGatheringCreateCommand command) throws IOException {
		// 유저가 속한 모임의 gatherId인지 확인
		gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
			.gatherId(command.getGatherId())
			.userId(getUserId())
			.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// exhId 존재하는 전시회 아이디인지 확인
		ExhEntity storedExhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 전시회 일정에 맞춰 갈 수 있는지 확인
		if (storedExhEntity.getExhPeriodStart().isAfter(command.getVisitDate())
			|| storedExhEntity.getExhPeriodEnd().isBefore(command.getVisitDate())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
		}
		// 관람 날짜 추가
		ExhVisitEntity newExhVisit = ExhVisitEntity.builder()
			.visitDate(command.getVisitDate())
			.gatherId(command.getGatherId())
			.exhId(storedExhEntity.getExhId())
			.build();
		// 관람 날짜 중복 확인
		Optional<ExhVisitEntity> checkExhVisit = exhVisitRepository.findByGatherIdAndExhIdAndVisitDate(
			command.getGatherId(), storedExhEntity.getExhId(), command.getVisitDate());

		if (checkExhVisit.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		try {
			exhVisitRepository.save(newExhVisit);
		} catch (DataIntegrityViolationException e) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}

		/* 반환 - 모임의 전시회 리스트 */
		// 반환 (모임에서 작성한 글들의 평점?으로 구현함.)
		List<FindGatheringExhResult> result = new ArrayList<>();
		// 모임이 갔다 온 각 전시회의 평점 구하기
		List<Map<String, Object>> gatherDiarySumRateAndCountList = diaryRepository.getGatherDiarySumRateAndCount(
			getUserId(), command.getGatherId());

		for (Map<String, Object> gatherDiarySumRateAndCount : gatherDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)gatherDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)gatherDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)gatherDiarySumRateAndCount.get("exhibition");
			LocalDate visitDate = (LocalDate)gatherDiarySumRateAndCount.get("visitDate");
			// averageRate & poster
			double averageRate = 0.0;

			if (sumOfRate != null && countOfDiary != null && countOfDiary != 0) {
				averageRate = sumOfRate / countOfDiary;
			}
			result.add(FindGatheringExhResult.findByGatheringExh(exh, averageRate));
		}

		// 모임 멤버들에게 푸시 알림 보내기
		sendPushAlarmToGatheringMember(command.getGatherId(), command.getVisitDate());
		return result;
	}

	@Override
	public List<FindGatheringDiaryResult> getDiariesAboutGatheringExh(GatheringDiariesFindQuery query) {
		// gather 데이터
		GatheringEntity gatheringEntity = gatheringRepository.findByGatherId(query.getGatherId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 모임에 포함되어 있는지 확인
		gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
			.userId(getUserId())
			.gatherId(gatheringEntity.getGatherId())
			.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// exh 전시회 존재 여부 확인
		ExhEntity exh = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 다이어리 반환
		List<Map<String, Object>> diaryList = diaryRepository.getGatherDiaryList(gatheringEntity.getGatherId(),
			exh.getExhId());
		List<FindGatheringDiaryResult> results = new ArrayList<>();

		for (Map<String, Object> item : diaryList) {
			DiaryEntity diary = (DiaryEntity)item.get("diaryEntity");
			ExhVisitEntity exhVisit = (ExhVisitEntity)item.get("exhVisitEntity");
			GatheringEntity gathering = (GatheringEntity)item.get("gatheringEntity");
			UserEntity user = (UserEntity)item.get("userEntity");

			if (diary != null && exhVisit != null) {
				// 비공개이지만 내가 작성한 경우
				if (!(diary.getDiaryPrivate() || Objects.equals(diary.getWriterId(), getUserId()))) {
					continue;
				}
				results.add(
					FindGatheringDiaryResult.findByGatheringDiary(diary, exhVisit, gathering, user, exh));
			}
		}
		results.sort(Comparator.comparing(FindGatheringDiaryResult::getInitDate));
		return results;
	}

	@Override
	public List<FindGatheringMatesResult> addGatheringMate(GatheringMateCreateCommand command) throws IOException {
		// 유저가 존재하는지 확인
		UserEntity requestGatheringMate = getUser(command.getUserId());
		// gatherId 확인
		GatheringEntity savedGathering = gatheringRepository.findByGatherId(command.getGatherId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 내가 모임에 속해 있는지 확인
		gatheringMateRepository.findByGatheringMateId(
				GatheringMateId.builder().gatherId(command.getGatherId()).userId(getUserId()).build())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 모임에 속해 있는 메이트 리스트 조회
		List<GatheringMateEntity> gatheringMateEntities = gatheringMateRepository.findByGatheringMateIdGatherId(
			command.getGatherId());

		for (GatheringMateEntity gatheringMate : gatheringMateEntities) {
			// 요청한 유저가 이미 모임에 있는지 확인
			if (gatheringMate.getGatheringMateId().getUserId().equals(requestGatheringMate.getUserId())) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			}
		}

		// 모임에 추가
		GatheringMateEntity gatheringMate = GatheringMateEntity.builder()
			.gatheringMateId(GatheringMateId.builder()
				.userId(requestGatheringMate.getUserId())
				.gatherId(command.getGatherId())
				.build())
			.build();
		try {
			gatheringMateRepository.save(gatheringMate);
		} catch (Exception e) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}

		// 기존 모임 메이트 리스트에 새로운 메이트 추가하여 gatheringMateEntities 재활용
		gatheringMateEntities.add(gatheringMate);

		List<FindGatheringMatesResult> results = new ArrayList<>();

		for (GatheringMateEntity gatheringMateEntity : gatheringMateEntities) {
			UserEntity mate = getUser(gatheringMateEntity.getGatheringMateId().getUserId());
			results.add(FindGatheringMatesResult.findByGatheringMates(mate));
		}

		// 초대 받은 사용자에게 푸시 알림 보내기
		sendPushAlarmToInvitedUser(requestGatheringMate, savedGathering);
		return results;
	}

	@Override
	public FindGatheringDetailInfoResult getGatheringDetailInfo(GatheringDetailInfoFindQuery query) {
		// 유저가 모임에 포함되어있는지 확인
		gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
			.userId(getUserId())
			.gatherId(query.getGatherId())
			.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		List<MateInfo> mateInfoList = new ArrayList<>();
		List<ExhibitionInfo> exhibitionInfoList = new ArrayList<>();
		// 1. gathering에 포함되어 있는 유저 리스트
		List<Map<String, Object>> gatheringMateList = gatheringMateRepository.getGatheringMateList(query.getGatherId());

		for (Map<String, Object> info : gatheringMateList) {
			UserEntity user = (UserEntity)info.get("user");

			mateInfoList.add(MateInfo.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.build());
		}
		// 2. gathering이 저장한 전시회 리스트(중복 제외)
		// 모임이 갔다 온 각 전시회의 평점 구하기 (모임에서 작성한 글들의 평점?으로 구현함.)
		List<Map<String, Object>> gatherDiarySumRateAndCountList = diaryRepository.getGatherDiarySumRateAndCount(
			getUserId(), query.getGatherId());

		for (Map<String, Object> gatherDiarySumRateAndCount : gatherDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)gatherDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)gatherDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)gatherDiarySumRateAndCount.get("exhibition");
			LocalDate visitDate = (LocalDate)gatherDiarySumRateAndCount.get("visitDate");
			// averageRate & poster
			double averageRate = 0.0;

			if (sumOfRate != null && countOfDiary != null && countOfDiary != 0) {
				averageRate = sumOfRate / countOfDiary;
			}
			exhibitionInfoList.add(ExhibitionInfo.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.poster(exh.getPoster())
				.rate(averageRate)
				.visitDate(changeDateFormat(visitDate))
				.build());
		}
		return FindGatheringDetailInfoResult.findByGatheringDetailInfo(mateInfoList, exhibitionInfoList);
	}

	@Override
	public FindIsGatheringMateResult searchNicknameNotInGathering(GatheringNicknameFindQuery query) {
		Long myUserId = getUserId();

		// 모임에 속해 있는지 확인
		Optional<GatheringMateEntity> isMember = gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
			.gatherId(query.getGatherId())
			.userId(myUserId)
			.build());

		if (isMember.isEmpty()) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 내 전시 리스트 중, 이미 모임에 포함된 경우와 아닌 경우로 나눠서 반환하기
		List<Map<String, Object>> gatheringMateQuery = gatheringMateRepository.getGatheringMateListForSearch(
			query.getGatherId(), myUserId, query.getNickname());
		List<FindGatheringMatesResult> alreadyMate = new ArrayList<>();
		List<FindGatheringMatesResult> notMate = new ArrayList<>();

		for (Map<String, Object> gatheringMate : gatheringMateQuery) {
			UserEntity userEntity = (UserEntity)gatheringMate.get("userEntity");
			Boolean isMate = (Boolean)gatheringMate.get("isGatheringMate");

			if (Objects.equals(myUserId, userEntity.getUserId())) {
				continue;
			}
			if (isMate) {
				alreadyMate.add(FindGatheringMatesResult.findByGatheringMates(userEntity));
			} else {
				notMate.add(FindGatheringMatesResult.findByGatheringMates(userEntity));
			}
		}
		return FindIsGatheringMateResult.findByGatheringMate(alreadyMate, notMate);
	}

	@Override
	public void deleteMyGathering(Long gatherId) {

		GatheringMateId deleteGatheringMateId = GatheringMateId.builder()
			.gatherId(gatherId)
			.userId(getUserId())
			.build();
		GatheringMateEntity deleteEntity = gatheringMateRepository.findByGatheringMateId(deleteGatheringMateId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		gatheringMateRepository.delete(deleteEntity);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private UserEntity getUser(Long userId) {
		return userRepository.findByUserId(userId).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}

	private void sendPushAlarmToInvitedUser(UserEntity invitedUser, GatheringEntity gathering) throws IOException {
		// 초대받는 사람의 알림이 켜져있어야 하고, 알림 토큰이 있어야 한다.
		if (invitedUser.getNewGatheringAlarm() && invitedUser.getAlarmToken() != null) {
			pushAlarm.sendMessageTo(FcmSendDto.builder().token(invitedUser.getAlarmToken())
				.title("새로운 모임 초대 알림")
				.body("\"" + gathering.getGatherName() + "\"에서 초대했어요. 모임 정보를 보려면 눌러주세요!")
				.type("gathering")
				.gatherId(gathering.getGatherId())
				.build());
		}
	}

	private void sendPushAlarmToGatheringMember(Long gatherId, LocalDate visitDate) throws
		IOException {
		// 모임의 멤버들의 NewGatheringAlarm 푸시 알림이 켜져있어야 하고, 알림 토큰이 있어야 한다.
		List<Map<String, Object>> gatheringMateList = gatheringMateRepository.getGatheringMateList(gatherId);
		String date = changeDateFormat(visitDate);

		for (Map<String, Object> info : gatheringMateList) {
			UserEntity user = (UserEntity)info.get("user");
			GatheringEntity gathering = (GatheringEntity)info.get("gathering");

			if (Objects.equals(user.getUserId(), getUserId())) {
				continue;
			}
			if (user.getNewDateGatheringAlarm() && user.getAlarmToken() != null) {
				pushAlarm.sendMessageTo(FcmSendDto.builder().token(user.getAlarmToken())
					.title("\"" + gathering.getGatherName() + "\"" + "와 함께 보러 갈 새로운 일정 알림")
					.body(date + "에 전시회 관람 날짜가 추가됐어요. 일정을 확인하려면 눌러주세요.")
					.type("calendar")
					.build());
			}
		}
	}
}
