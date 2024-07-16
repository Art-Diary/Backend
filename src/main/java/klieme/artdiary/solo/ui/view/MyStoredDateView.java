package klieme.artdiary.solo.ui.view;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.info.StoredDateInfo;
import klieme.artdiary.solo.service.MyExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyStoredDateView {
	private final Long index;
	private final Long exhId;
	// "내 기록의 전시회 방문 날짜 추가"의 반환 데이터
	private final Long exhVisitId;
	private final String visitDate;
	// "한 전시회에 대하여 캘린더에 저장된 날짜 조회"의 반환 데이터
	private final Long gatherId; // 개인일 경우엔 null
	private final String gatherName; // 개인일 경우엔 null
	private final List<StoredDateInfo> dateInfoList;

	@Builder
	public MyStoredDateView(Long index, MyExhReadUseCase.FindMyStoredDateResult result) {
		this.index = index;
		this.exhId = result.getExhId();
		this.gatherId = result.getGatherId();
		this.gatherName = result.getGatherName();
		this.exhVisitId = result.getExhVisitId();
		this.visitDate = result.getVisitDate();
		this.dateInfoList = result.getDateInfoList();
	}
}
