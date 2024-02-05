package klieme.artdiary.gatherings.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringView {
	private final Long gatherId;
	private final String gatherName;

	@Builder
	public GatheringView(GatheringReadUseCase.FindGatheringResult result) {
		this.gatherId = result.getGatherId();
		this.gatherName = result.getGatherName();
	}
}
