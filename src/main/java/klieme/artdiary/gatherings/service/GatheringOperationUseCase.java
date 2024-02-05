package klieme.artdiary.gatherings.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringOperationUseCase {

	GatheringReadUseCase.FindGatheringResult createGathering(GatheringCreateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringCreateCommand {
		private final String gatherName;
	}
}
