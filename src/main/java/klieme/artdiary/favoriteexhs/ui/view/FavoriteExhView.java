package klieme.artdiary.favoriteexhs.ui.view;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.favoriteexhs.service.FavoriteExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteExhView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final LocalDate exhPeriodStart;
	private final LocalDate exhPeriodEnd;
	private final String poster;
	private final Boolean favoriteExh;

	@Builder
	public FavoriteExhView(FavoriteExhReadUseCase.FindFavoriteExhResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.exhPeriodStart = result.getExhPeriodStart();
		this.exhPeriodEnd = result.getExhPeriodEnd();
		this.poster = result.getPoster();
		this.favoriteExh = result.getFavoriteExh();
	}
}
