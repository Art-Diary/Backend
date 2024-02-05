package klieme.artdiary.gatherings.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;

@Service
public class GatheringService implements GatheringOperationUseCase, GatheringReadUseCase {
	private final GatheringRepository gatheringRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final ExhRepository exhRepository;
	private final GatheringExhRepository gatheringExhRepository;

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, GatheringMateRepository gatheringMateRepository,
		ExhRepository exhRepository, GatheringExhRepository gatheringExhRepository) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.exhRepository = exhRepository;
		this.gatheringExhRepository = gatheringExhRepository;
	}

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
		List<GatheringReadUseCase.FindGatheringExhsResult> result = new ArrayList<>();
		// 모임이 저장한 전시회 리스트 요청
		List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherId(command.getGatherId());
		// 같은 전시회지만 방문 날짜가 다른 경우를 중복을 없애기 위해 사용
		List<Long> checkExhs = new ArrayList<>();

		for (GatheringExhEntity entity : gatheringExhEntityList) {
			if (!checkExhs.contains(entity.getExhId())) { // 이미 얻은 전시회인지 확인
				// 전시회 정보 조회
				Optional<ExhEntity> exh = exhRepository.findByExhId(entity.getExhId());
				if (exh.isPresent()) {
					/* TODO
					 * 저장된 포스터 사진 있으면 구현
					 * 전시회 별점 - 개인 + 모임들의 별점 합산 => 기록이 있어야 함.
					 * 아래 코드의 null 수정 필요
					 */
					// 포스터 사진
					// 전시회 별점 - 개인 + 모임들의 별점 합산 => 기록이 있어야 함.
					result.add(GatheringReadUseCase.FindGatheringExhsResult.findByGatheringExhs(exh.get(), null, null));
				}
				checkExhs.add(entity.getExhId());
			}
		}
		return result;
	}

	private Long getUserId() {
		return 4L;
	}
}
