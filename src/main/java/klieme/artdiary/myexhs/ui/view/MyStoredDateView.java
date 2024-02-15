package klieme.artdiary.myexhs.ui.view;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.myexhs.service.MyExhsReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyStoredDateView {
	private final Long exhId;
	private final Long gatherId; // 개인일 경우엔 null
	private final Long gatheringExhId; // 개인일 경우엔 null
	private final String gatherName; // 개인일 경우엔 null
	private final Long userExhId; // 모임일 경우엔 null
	private final List<LocalDate> dates;

	@Builder
	public MyStoredDateView(MyExhsReadUseCase.FindMyStoredDateResult result) {
		this.exhId = result.getExhId();
		this.gatherId = result.getGatherId();
		this.gatheringExhId = result.getGatheringExhId();
		this.gatherName = result.getGatherName();
		this.userExhId = result.getUserExhId();
		this.dates = result.getDates();
	}
}
