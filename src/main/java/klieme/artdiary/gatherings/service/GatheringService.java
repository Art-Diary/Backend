package klieme.artdiary.gatherings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;

@Service
public class GatheringService implements GatheringOperationUseCase {
	private final GatheringRepository gatheringRepository;
	private final GatheringMateRepository gatheringMateRepository;

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, GatheringMateRepository gatheringMateRepository) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMateRepository = gatheringMateRepository;
	}

	@Override
	public GatheringReadUseCase.FindGatheringResult createGathering(GatheringCreateCommand command) {
		GatheringEntity gatheringEntity = GatheringEntity.builder()
			.gatherName(command.getGatherName())
			.build();
		gatheringRepository.save(gatheringEntity);
		GatheringMateEntity mateEntity = GatheringMateEntity.builder()
			.gatheringMateId(GatheringMateId.builder()
				.gatherId(gatheringEntity.getGatherId())
				.userId(getUserId())
				.build())
			.build();
		gatheringMateRepository.save(mateEntity);
		return GatheringReadUseCase.FindGatheringResult.findByGathering(gatheringEntity);
	}

	private Long getUserId() {
		return 3L;
	}
}
