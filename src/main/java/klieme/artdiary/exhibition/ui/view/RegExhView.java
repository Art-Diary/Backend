package klieme.artdiary.exhibition.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.service.RegExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegExhView {
	private final Long regExhId;
	private final String regExhName;
	private final String regGallery;
	private final String regExhPeriodStart;
	private final String regExhPeriodEnd;
	private final String regPainter;
	private final Integer regFee;
	private final String regIntro;
	private final String regUrl;
	private final String regPoster;
	private final String regArt;
	private final String regDate;
	private final String regComment;
	private final String regState;
	private final String regSource;

	@Builder
	public RegExhView(RegExhReadUseCase.FindRegExhResult result) {
		this.regExhId = result.getRegExhId();
		this.regExhName = result.getRegExhName();
		this.regGallery = result.getRegGallery();
		this.regExhPeriodStart = result.getRegExhPeriodStart();
		this.regExhPeriodEnd = result.getRegExhPeriodEnd();
		this.regPainter = result.getRegPainter();
		this.regFee = result.getRegFee();
		this.regIntro = result.getRegIntro();
		this.regUrl = result.getRegUrl();
		this.regPoster = result.getRegPoster();
		this.regArt = result.getRegArt();
		this.regDate = result.getRegDate();
		this.regComment = result.getRegComment();
		this.regState = result.getRegState();
		this.regSource = result.getRegSource();
	}
}
