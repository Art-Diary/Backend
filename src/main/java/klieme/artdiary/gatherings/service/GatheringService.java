package klieme.artdiary.gatherings.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;
import klieme.artdiary.gatherings.info.ExhibitionInfo;
import klieme.artdiary.gatherings.info.MateInfo;
import klieme.artdiary.mate.data_access.entity.MateEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class GatheringService implements GatheringOperationUseCase, GatheringReadUseCase {
	private final GatheringRepository gatheringRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final ExhRepository exhRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final UserRepository userRepository;
	private final MateRepository mateRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, GatheringMateRepository gatheringMateRepository,
		ExhRepository exhRepository, GatheringExhRepository gatheringExhRepository,
		GatheringDiaryRepository gatheringDiaryRepository, UserRepository userRepository,
		MateRepository mateRepository, ImageTransfer imageTransfer) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.exhRepository = exhRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.userRepository = userRepository;
		this.mateRepository = mateRepository;
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
	public List<GatheringReadUseCase.FindGatheringExhsResult> addExhAboutGathering(
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
		// 모임에 이미 저장된 날짜인지 확인
		Optional<GatheringExhEntity> storedGatheringExhEntity = gatheringExhRepository.findByGatherIdAndExhIdAndVisitDate(
			command.getGatherId(), command.getExhId(), command.getVisitDate());
		if (storedGatheringExhEntity.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 전시회 일정에 맞춰 갈 수 있는지 확인
		if (storedExhEntity.getExhPeriodStart().isAfter(command.getVisitDate())
			|| storedExhEntity.getExhPeriodEnd().isBefore(command.getVisitDate())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
		}
		// 모임의 일정에 추가
		GatheringExhEntity addGatheringExhEntity = GatheringExhEntity.builder()
			.visitDate(command.getVisitDate())
			.gatherId(command.getGatherId())
			.exhId(command.getExhId())
			.build();
		gatheringExhRepository.save(addGatheringExhEntity);

		/* 반환 - 모임의 전시회 리스트 */
		// 반환 (모임에서 작성한 글들의 평점?으로 구현함.)
		List<GatheringReadUseCase.FindGatheringExhsResult> result = new ArrayList<>();
		// 모임이 갔다 온 각 전시회의 평점 구하기
		List<Pair<Long, Double>> averages = getExhAverageRate(command.getGatherId());

		for (Pair<Long, Double> average : averages) {
			Optional<ExhEntity> exh = exhRepository.findByExhId(average.getFirst());

			if (exh.isPresent()) {
				String poster = imageTransfer.downloadImage(exh.get().getPoster());
				result.add(FindGatheringExhsResult.findByGatheringExhs(exh.get(), poster, average.getSecond()));
			}

		}
		return result;
	}

	@Override
	public List<FindGatheringDiaryResult> getDiariesAboutGatheringExh(GatheringDiariesFindQuery query) throws
		IOException {
		// 모임에 포함되어 있는지 확인
		gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
			.userId(getUserId())
			.gatherId(query.getGatherId())
			.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// gather 데이터
		GatheringEntity gathering = gatheringRepository.findByGatherId(query.getGatherId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// exh 전시회 존재 여부 확인
		ExhEntity exh = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// gatherExh 테이블에서 gatherId & exhId 리스트 조회
		List<GatheringExhEntity> gatheringExhEntities = gatheringExhRepository.findByGatherIdAndExhId(
			query.getGatherId(),
			query.getExhId());

		if (gatheringExhEntities.isEmpty()) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 반환 리스트
		List<FindGatheringDiaryResult> results = new ArrayList<>();
		// gatherDiary 테이블에서 gatherExhId로 다이어리 리스트 조회
		for (GatheringExhEntity gatheringExh : gatheringExhEntities) {
			List<GatheringDiaryEntity> gatheringDiaryEntities = gatheringDiaryRepository.findByGatheringExhId(
				gatheringExh.getGatheringExhId());
			for (GatheringDiaryEntity gatheringDiary : gatheringDiaryEntities) {
				if (!gatheringDiary.getDiaryPrivate()) {
					continue;
				}
				String thumbnail = imageTransfer.downloadImage(gatheringDiary.getThumbnail());
				UserEntity writer;

				// 유저가 탈퇴하여 userId가 -null인 경우 고려
				if (gatheringDiary.getUserId() == null) {
					writer = UserEntity.builder().nickname("전시 메이트").build();
				} else {
					writer = userRepository.findByUserId(gatheringDiary.getUserId())
						.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
				}
				results.add(
					FindGatheringDiaryResult.findByGatheringDiary(gatheringDiary, gatheringExh, gathering, writer, exh,
						thumbnail));
			}
		}
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
		List<Pair<Long, Double>> averages = getExhAverageRate(query.getGatherId());
		for (Pair<Long, Double> average : averages) {
			Optional<ExhEntity> exh = exhRepository.findByExhId(average.getFirst());

			if (exh.isPresent()) {
				String poster = imageTransfer.downloadImage(exh.get().getPoster());
				exhibitionInfoList.add(ExhibitionInfo.builder()
					.exhId(exh.get().getExhId())
					.exhName(exh.get().getExhName())
					.poster(poster)
					.rate(average.getSecond())
					.build());
			}

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

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}

	private UserEntity getUser(Long userId) {
		return userRepository.findByUserId(userId).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}

	// 모임이 갔다 온 각 전시회의 평점 구하기
	private List<Pair<Long, Double>> getExhAverageRate(Long gatheringId) {
		List<Pair<Long, Double>> results = new ArrayList<>(); // 반환
		List<Long> checkExhs = new ArrayList<>(); // 같은 전시회지만 방문 날짜가 다른 경우의 중복을 없애기 위해 사용
		HashMap<Long, Integer> countDiary = new HashMap<>(); // 전시회에 대한 기록 개수
		HashMap<Long, Double> sumDiaryRate = new HashMap<>(); // 전시회에 대한 기록 별점 합
		// 모임이 저장한 전시회 리스트 조회
		List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherId(gatheringId);
		// 각 전시회의 기록들을 조회하여 별점 합 얻기
		for (GatheringExhEntity gatheringExh : gatheringExhEntityList) {
			// gatheringDiary에서 기록 조회
			List<GatheringDiaryEntity> gatheringDiaryEntities = gatheringDiaryRepository.findByGatheringExhId(
				gatheringExh.getGatheringExhId());

			countDiary.putIfAbsent(gatheringExh.getExhId(), 0);
			sumDiaryRate.putIfAbsent(gatheringExh.getExhId(), 0.0);
			// 기록들의 별점 합과 개수 구하기
			for (GatheringDiaryEntity gatheringDiary : gatheringDiaryEntities) {
				Integer countExh = countDiary.get(gatheringExh.getExhId());
				Double sumExhRate = sumDiaryRate.get(gatheringExh.getExhId());
				countDiary.put(gatheringExh.getExhId(), countExh + 1);
				sumDiaryRate.put(gatheringExh.getExhId(), sumExhRate + gatheringDiary.getRate());
			}
			// 전시회 중복 제거
			if (!checkExhs.contains(gatheringExh.getExhId())) {
				checkExhs.add(gatheringExh.getExhId());
			}
		}
		for (Long exhId : checkExhs) {
			Double averageRate = countDiary.get(exhId) == 0 ? 0.0 : sumDiaryRate.get(exhId) / countDiary.get(exhId);
			results.add(Pair.of(exhId, averageRate));
		}
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
}
