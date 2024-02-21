package klieme.artdiary.favoriteexhs.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface FavoriteExhOperationUseCase {
	FavoriteExhReadUseCase.FindFavoriteExhResult createFavoriteExh(FavoriteExhCreateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class FavoriteExhCreateCommand {
		private final Long exhId;
	}
}
