package klieme.artdiary.favoriteexhs.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.favoriteexhs.data_access.entity.FavoriteExhEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface FavoriteExhReadUseCase {

	List<FindFavoriteExhResult> getFavoriteExhs();

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

		public static FindFavoriteExhResult findByFavoriteExhDetail(ExhEntity exh) {
			return FindFavoriteExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(exh.getPoster())
				.favoriteExh(true)
				.build();
		}
	}
}
