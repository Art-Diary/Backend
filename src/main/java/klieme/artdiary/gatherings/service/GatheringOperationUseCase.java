package klieme.artdiary.gatherings.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringOperationUseCase {

	GatheringReadUseCase.FindGatheringResult createGathering(GatheringCreateCommand command);

	List<GatheringReadUseCase.FindGatheringExhsResult> addExhAboutGathering(ExhGatheringCreateCommand command);

	List<GatheringReadUseCase.FindGatheringMatesResult> addGatheringMate(GatheringMateCreateCommand command);

	void deleteMyGathering(Long gatherId);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringCreateCommand {
		private final String gatherName;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class ExhGatheringCreateCommand {
		private final Long gatherId;
		private final Long exhId;
		private final LocalDate visitDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringMateCreateCommand {
		private final Long gatherId;
		private final Long userId;
	}
}
