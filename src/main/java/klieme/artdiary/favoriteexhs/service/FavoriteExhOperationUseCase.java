package klieme.artdiary.favoriteexhs.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface FavoriteExhOperationUseCase {
	FavoriteExhReadUseCase.FindFavoriteExhResult createFavoriteExh(FavoriteExhCreateCommand command);

	void deleteFavoriteExh(List<FavoriteExhCreateCommand> commands);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class FavoriteExhCreateCommand {
		private final Long exhId;
	}
}
