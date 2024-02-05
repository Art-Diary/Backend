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
}
