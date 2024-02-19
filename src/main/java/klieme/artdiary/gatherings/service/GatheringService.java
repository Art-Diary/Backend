package klieme.artdiary.gatherings.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
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

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, GatheringMateRepository gatheringMateRepository,
		ExhRepository exhRepository, GatheringExhRepository gatheringExhRepository,
		GatheringDiaryRepository gatheringDiaryRepository, UserRepository userRepository) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.exhRepository = exhRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.userRepository = userRepository;
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
	public List<GatheringReadUseCase.FindGatheringExhsResult> addExhAboutGathering(ExhGatheringCreateCommand command) {
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
		List<Long> checkExhs = new ArrayList<>(); // 같은 전시회지만 방문 날짜가 다른 경우의 중복을 없애기 위해 사용
		HashMap<Long, Integer> countDiary = new HashMap<>(); // 전시회에 대한 기록 개수
		HashMap<Long, Double> sumDiaryRate = new HashMap<>(); // 전시회에 대한 기록 별점 합
		// 모임이 저장한 전시회 리스트 조회
		List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherId(command.getGatherId());
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
		// 반환
		List<GatheringReadUseCase.FindGatheringExhsResult> result = new ArrayList<>();
		for (Long exhId : checkExhs) {
			Optional<ExhEntity> exh = exhRepository.findByExhId(exhId);
			/* TODO
			 * 모임에서 작성한 글들의 평점?으로 구현함.
			 * 저장된 포스터 사진 있으면 구현
			 * 아래 코드의 null 수정 필요
			 */
			Double averageRate = countDiary.get(exhId) == 0 ? 0.0 : sumDiaryRate.get(exhId) / countDiary.get(exhId);
			exh.ifPresent(exhEntity -> result.add(FindGatheringExhsResult.findByGatheringExhs(exhEntity, null,
				averageRate)));
		}
		return result;
	}

	@Override
	public List<FindGatheringDiaryResult> getDiariesAboutGatheringExh(GatheringDiariesFindQuery query) {
		UserEntity user = getUser();
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
				results.add(
					FindGatheringDiaryResult.findByGatheringDiary(gatheringDiary, gatheringExh, gathering, user, exh));
			}
		}
		return results;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}

	private UserEntity getUser() {
		return userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}
}
