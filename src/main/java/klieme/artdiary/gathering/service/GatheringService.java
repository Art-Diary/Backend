package klieme.artdiary.gathering.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMateId;
import klieme.artdiary.gathering.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.gathering.info.ExhibitionInfo;
import klieme.artdiary.gathering.info.MateInfo;
import klieme.artdiary.mate.data_access.entity.MateEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
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
	private final MateRepository mateRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, GatheringMateRepository gatheringMateRepository,
		ExhRepository exhRepository, UserRepository userRepository, MateRepository mateRepository,
		ExhVisitRepository exhVisitRepository, DiaryRepository diaryRepository, ImageTransfer imageTransfer) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.exhRepository = exhRepository;
		this.userRepository = userRepository;
		this.mateRepository = mateRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.imageTransfer = imageTransfer;
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

		// 모임 있는지 확인
		List<GatheringMateEntity> GEntities = gatheringMateRepository.findByGatheringMateIdUserId(userId);

		//if (query.getGatherId() == null) {
		// (목적) 한 전시회에 대한 캘린더에 저장된 개인의 일정 날짜 조회 로직 구현
		//List<GatheringEntity> entities = GatheringMateRepository.findByUserIdAndGatherId(userId, query.getGatherId());
		for (GatheringMateEntity GEntity : GEntities) {
			GatheringEntity gatheringEntity = gatheringRepository.findByGatherId(
				GEntity.getGatheringMateId().getGatherId()).orElseThrow(() -> new ArtDiaryException(
				MessageType.NOT_FOUND));
			gatherings.add(GatheringReadUseCase.FindGatheringResult.findByGathering(gatheringEntity));
		}
		//}

		return gatherings;
	}

	@Transactional
	@Override
	public List<FindGatheringExhResult> addExhAboutGathering(
		ExhGatheringCreateCommand command) throws
		IOException {
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
			command.getGatherId());

		for (Map<String, Object> gatherDiarySumRateAndCount : gatherDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)gatherDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)gatherDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)gatherDiarySumRateAndCount.get("exhibition");
			// averageRate & poster
			double averageRate = 0.0;
			String poster = imageTransfer.downloadImage(exh.getPoster());

			if (sumOfRate != null && countOfDiary != null && countOfDiary != 0) {
				averageRate = sumOfRate / countOfDiary;
			}
			result.add(FindGatheringExhResult.findByGatheringExh(exh, poster, averageRate));
		}
		return result;
	}

	@Override
	public List<FindGatheringDiaryResult> getDiariesAboutGatheringExh(GatheringDiariesFindQuery query) throws
		IOException {
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
				String thumbnail = imageTransfer.downloadImage(diary.getThumbnail());

				if (user == null) {
					user = UserEntity.builder().nickname("전시 메이트").build();
				}
				results.add(
					FindGatheringDiaryResult.findByGatheringDiary(diary, exhVisit, gathering, user, exh, thumbnail));
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
		// 모임에 속해 있는 메이트 리스트 조회
		List<GatheringMateEntity> gatheringMateEntities = gatheringMateRepository.findByGatheringMateIdGatherId(
			savedGathering.getGatherId());
		boolean checkImInGathering = false;
		boolean checkRequestUserInGathering = false;
		for (GatheringMateEntity gatheringMate : gatheringMateEntities) {
			if (gatheringMate.getGatheringMateId().getUserId().equals(getUserId())) {
				checkImInGathering = true;
			}
			if (gatheringMate.getGatheringMateId().getUserId().equals(requestGatheringMate.getUserId())) {
				checkRequestUserInGathering = true;
			}
		}

		// 내가 모임에 속해 있는지 확인 || 요청한 유저가 이미 모임에 있는지 확인
		if (!checkImInGathering) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		if (checkRequestUserInGathering) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}

		// 모임에 추가
		GatheringMateEntity gatheringMate = GatheringMateEntity.builder()
			.gatheringMateId(GatheringMateId.builder()
				.userId(requestGatheringMate.getUserId())
				.gatherId(savedGathering.getGatherId())
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
			String profile = imageTransfer.downloadImage(mate.getProfile());
			results.add(FindGatheringMatesResult.findByGatheringMates(mate, profile));
		}
		return results;
	}

	@Override
	public FindGatheringDetailInfoResult getGatheringDetailInfo(GatheringDetailInfoFindQuery query) throws IOException {
		// 유저가 모임에 포함되어있는지 확인
		gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
			.userId(getUserId())
			.gatherId(query.getGatherId())
			.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		List<MateInfo> mateInfoList = new ArrayList<>();
		List<ExhibitionInfo> exhibitionInfoList = new ArrayList<>();
		// 1. gathering에 포함되어 있는 유저 리스트
		// gatheringMate에서 gatherId로 조회
		List<GatheringMateEntity> gatheringMateList = gatheringMateRepository.findByGatheringMateIdGatherId(
			query.getGatherId());
		// user에서 조회
		for (GatheringMateEntity gatheringMate : gatheringMateList) {
			UserEntity user = userRepository.findByUserId(gatheringMate.getGatheringMateId().getUserId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			mateInfoList.add(MateInfo.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.build());
		}
		// 2. gathering이 저장한 전시회 리스트(중복 제외)
		// 모임이 갔다 온 각 전시회의 평점 구하기 (모임에서 작성한 글들의 평점?으로 구현함.)
		List<Map<String, Object>> gatherDiarySumRateAndCountList = diaryRepository.getGatherDiarySumRateAndCount(
			query.getGatherId());

		for (Map<String, Object> gatherDiarySumRateAndCount : gatherDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)gatherDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)gatherDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)gatherDiarySumRateAndCount.get("exhibition");
			// averageRate & poster
			double averageRate = 0.0;
			String poster = imageTransfer.downloadImage(exh.getPoster());

			if (sumOfRate != null && countOfDiary != null && countOfDiary != 0) {
				averageRate = sumOfRate / countOfDiary;
			}
			exhibitionInfoList.add(ExhibitionInfo.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.poster(poster)
				.rate(averageRate)
				.build());
		}
		return FindGatheringDetailInfoResult.findByGatheringDetailInfo(mateInfoList, exhibitionInfoList);
	}

	@Override
	public List<FindGatheringMatesResult> searchNicknameNotInGathering(GatheringNicknameFindQuery query) throws
		IOException {
		// 모임 멤버 리스트 조회
		List<GatheringMateEntity> gatheringMateEntities = gatheringMateRepository.findByGatheringMateIdGatherId(
			query.getGatherId());
		// 모임에 속해 있는지 확인
		Optional<GatheringMateEntity> isMember = gatheringMateEntities.stream()
			.filter(gm -> gm.getGatheringMateId().getUserId().equals(getUserId()))
			.findAny();

		if (isMember.isEmpty()) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 요청 nickname에 해당하면서 모임에 속해 있지 않은 유저 필터링
		// 내 전시 메이트 중에서 확인
		List<MateEntity> mateEntities = mateRepository.findByFromUserId(getUserId());
		List<FindGatheringMatesResult> results = new ArrayList<>();

		for (MateEntity mate : mateEntities) {
			Optional<UserEntity> user = userRepository.findByUserIdAndNicknameContainingIgnoreCase(mate.getToUserId(),
				query.getNickname());

			if (user.isPresent()) {
				Optional<GatheringMateEntity> filterUser = gatheringMateEntities.stream()
					.filter(gm -> gm.getGatheringMateId().getUserId().equals(user.get().getUserId()))
					.findAny();

				if (filterUser.isEmpty()) {
					String profile = imageTransfer.downloadImage(user.get().getProfile());
					results.add(FindGatheringMatesResult.findByGatheringMates(user.get(), profile));
				}
			}
		}
		// 이름 순으로 정렬
		results.sort(Comparator.comparing(FindGatheringMatesResult::getNickname));
		return results;
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
		return UserIdFilter.getUserId();
	}

	private UserEntity getUser(Long userId) {
		return userRepository.findByUserId(userId).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}
}
