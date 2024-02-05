package klieme.artdiary.gatherings.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringExhView {
	private final Long exhId;
	private final String exhName;
	private final String poster;
	private final Double rate;

	@Builder
	public GatheringExhView(GatheringReadUseCase.FindGatheringExhsResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.poster = result.getPoster();
		this.rate = result.getRate();
	}
}
