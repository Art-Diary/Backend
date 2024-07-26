package klieme.artdiary.favoriteexh.service;

import static klieme.artdiary.common.FormatDate.*;

import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhEntity;
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
		private final String exhPeriodStart;
		private final String exhPeriodEnd;
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
				.exhPeriodStart(changeDateFormat(exh.getExhPeriodStart()))
				.exhPeriodEnd(changeDateFormat(exh.getExhPeriodEnd()))
				.poster(exh.getPoster())
				.favoriteExh(true)
				.build();
		}
	}
}
