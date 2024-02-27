package klieme.artdiary.gatherings.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gatherings.info.ExhibitionInfo;
import klieme.artdiary.gatherings.info.MateInfo;
import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringDetailInfoView {
	private final List<MateInfo> mates;
	private final List<ExhibitionInfo> exhibitions;

	@Builder
	public GatheringDetailInfoView(GatheringReadUseCase.FindGatheringDetailInfoResult result) {
		this.mates = result.getMates();
		this.exhibitions = result.getExhibitions();
	}
}
