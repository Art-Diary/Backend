package klieme.artdiary.exhibition.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.service.ExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExhView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String exhPeriodStart;
	private final String exhPeriodEnd;
	private final String poster;
	private final Boolean favoriteExh;
	private final String painter;
	private final Integer fee;
	private final String intro;
	private final String url;
	private final String art;
	private final String source;

	@Builder
	public ExhView(ExhReadUseCase.FindExhResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.exhPeriodStart = result.getExhPeriodStart();
		this.exhPeriodEnd = result.getExhPeriodEnd();
		this.poster = result.getPoster();
		this.favoriteExh = result.getFavoriteExh();
		this.painter = result.getPainter();
		this.fee = result.getFee();
		this.intro = result.getIntro();
		this.url = result.getUrl();
		this.art = result.getArt();
		this.source = result.getSource();
	}
}
