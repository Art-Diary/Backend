package klieme.artdiary.favoriteexhs.service;

import java.time.LocalDate;

import klieme.artdiary.favoriteexhs.data_access.entity.FavoriteExhEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface FavoriteExhReadUseCase {
	@Getter
	@ToString
	@Builder
	class FindFavoriteExhResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final LocalDate exhPeriodStart;
		private final LocalDate exhPeriodEnd;
		private final String poster;
		private final Boolean favoriteExh;

		public static FindFavoriteExhResult findByFavoriteExh(FavoriteExhEntity favoriteExh) {
			return FindFavoriteExhResult.builder()
				.exhId(favoriteExh.getFavoriteExhId().getExhId())
				.build();
		}
	}
}
