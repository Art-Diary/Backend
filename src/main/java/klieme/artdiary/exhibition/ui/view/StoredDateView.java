package klieme.artdiary.exhibition.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.info.StoredListOfDate;
import klieme.artdiary.exhibition.service.ExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoredDateView {
	private final Long exhId;
	private final Long gatherId; // 개인일 경우엔 null
	private final String gatherName; // 개인일 경우엔 null
	private final List<StoredListOfDate> dateInfoList;

	@Builder
	public StoredDateView(ExhReadUseCase.FindStoredDateResult result) {
		this.exhId = result.getExhId();
		this.gatherId = result.getGatherId();
		this.gatherName = result.getGatherName();
		this.dateInfoList = result.getDates();
	}
}
