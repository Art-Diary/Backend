package klieme.artdiary.mateexhs.ui.view;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.mateexhs.service.MateExhsReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateDiaryView {

	private final Long diaryId;
	private final String title;
	private final Double rate;
	private final Boolean diaryPrivate;
	private final String contents;
	private final String thumbnail;
	private final LocalDate writeDate;
	private final String saying;
	private final String nickname;
	private final String gatherName;
	private final LocalDate visitDate;
	private final String exhName;
	private final Long userExhId;
	private final Long gatheringExhId;

	@Builder
	public MateDiaryView(MateExhsReadUseCase.FindMateDiaryResult result) {
		this.diaryId = result.getDiaryId();
		this.title = result.getTitle();
		this.rate = result.getRate();
		this.diaryPrivate = result.getDiaryPrivate();
		this.contents = result.getContents();
		this.thumbnail = result.getThumbnail();
		this.writeDate = result.getWriteDate();
		this.saying = result.getSaying();
		this.nickname = result.getNickname();
		this.gatherName = result.getGatherName();
		this.visitDate = result.getVisitDate();
		this.exhName = result.getExhName();
		this.userExhId = result.getUserExhId();
		this.gatheringExhId = result.getGatheringExhId();

	}

}