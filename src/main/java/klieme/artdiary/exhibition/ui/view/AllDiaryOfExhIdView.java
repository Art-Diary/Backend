package klieme.artdiary.exhibition.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.service.ExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllDiaryOfExhIdView {

	private final Long diaryId;
	private final Long exhVisitId;
	private final String title;
	private final Double rate;
	private final Boolean diaryPrivate;
	private final String contents;
	private final String thumbnail;
	private final String initDate;
	private final String writeDate;
	private final String saying;
	private final Long userId;
	private final String nickname; // 작성자
	private final String gatherName;
	private final String visitDate;
	private final String exhName;

	@Builder
	public AllDiaryOfExhIdView(ExhReadUseCase.FindDiaryResult result) {
		this.diaryId = result.getDiaryId();
		this.exhVisitId = result.getExhVisitId();
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
	}
}
