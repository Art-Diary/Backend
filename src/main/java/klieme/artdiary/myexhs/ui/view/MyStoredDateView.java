package klieme.artdiary.myexhs.ui.view;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.myexhs.info.StoredDateInfo;
import klieme.artdiary.myexhs.service.MyExhsReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyStoredDateView {
	private final Long index;
	private final Long exhId;
	private final Long gatherId; // 개인일 경우엔 null
	private final String gatherName; // 개인일 경우엔 null
	// private final Long gatheringExhId; // 개인일 경우엔 null
	private final Long userExhId; // 모임일 경우엔 null
	private final LocalDate visitDate;
	private final List<StoredDateInfo> dateInfoList;

	/* 변경된 response data 형식
	long exhId;
	long gatherId;
	string gatherName; (개인일 경우엔 null)
	date: [
		long gatheringExhId;
		long userExhId;
		localdate visitDate;
	]
	*/

	@Builder
	public MyStoredDateView(Long index, MyExhsReadUseCase.FindMyStoredDateResult result) {
		this.index = index;
		this.exhId = result.getExhId();
		this.gatherId = result.getGatherId();
		this.gatherName = result.getGatherName();
		this.userExhId = result.getUserExhId();
		this.visitDate = result.getVisitDate();
		this.dateInfoList = result.getDateInfoList();
	}
}
