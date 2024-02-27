package klieme.artdiary.exhibitions.ui.view;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllDiaryOfExhIdView {

	private final Long diaryId;
	private final String title;
	private final Double rate;
	private final Boolean diaryPrivate;
	private final String contents;
	private final String thumbnail;
	private final LocalDate writeDate;
	private final String saying;
	private final String nickname; // 작성자
	private final String gatherName;
	private final LocalDate visitDate;
	private final String exhName;
	private final Long userExhId; // 개인 일정이 아닌 경우 null
	private final Long gatheringExhId; // 모임이 아닐 경우 null

	@Builder
	public AllDiaryOfExhIdView(ExhReadUseCase.FindDiaryResult result) {

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
