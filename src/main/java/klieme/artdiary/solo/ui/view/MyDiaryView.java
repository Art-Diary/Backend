package klieme.artdiary.solo.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.service.MyDiaryReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyDiaryView {
	private final Long diaryId;
	private final String title;
	private final Double rate;
	private final Boolean diaryPrivate;
	private final String contents;
	private final String thumbnail;
	private final String initDate;
	private final String writeDate;
	private final String saying;
	private final Long userId;
	private final String nickname;
	private final String gatherName;
	private final String visitDate;
	private final String exhName;
	private final Long exhVisitId;

	@Builder
	public MyDiaryView(MyDiaryReadUseCase.FindMyDiaryResult result) {
		this.diaryId = result.getDiaryId();
		this.title = result.getTitle();
		this.rate = result.getRate();
		this.diaryPrivate = result.getDiaryPrivate();
		this.contents = result.getContents();
		this.thumbnail = result.getThumbnail();
		this.initDate = result.getInitDate();
		this.writeDate = result.getWriteDate();
		this.saying = result.getSaying();
		this.userId = result.getUserId();
		this.nickname = result.getNickname();
		this.gatherName = result.getGatherName();
		this.visitDate = result.getVisitDate();
		this.exhName = result.getExhName();
		this.exhVisitId = result.getExhVisitId();
	}
}
