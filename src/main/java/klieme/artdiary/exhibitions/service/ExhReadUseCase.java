package klieme.artdiary.exhibitions.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.enums.ExhField;
import klieme.artdiary.exhibitions.enums.ExhPrice;
import klieme.artdiary.exhibitions.enums.ExhState;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ExhReadUseCase {
	FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query);

	List<FindExhResult> getExhList(ExhListFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class ExhListFindQuery {
		private final String searchName;
		private final ExhCategory exhCategory;
		private final LocalDate date;

		@Builder
		@Getter
		public static class ExhCategory {
			private final ExhField field;
			private final ExhPrice price;
			private final ExhState state;
		}
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class StoredDateFindQuery {
		private final Long exhId;
		private final Long gatherId;
	}

	@Getter
	@ToString
	@Builder
	class FindStoredDateResult {
		private final Long exhId;
		private final LocalDate visitDate; // 단일 데이터일 때 사용
		private final List<LocalDate> dates; // 리스트 데이터일 떄 사용

		public static FindStoredDateResult findByStoredDate(Long exhId, LocalDate visitDate, List<LocalDate> dates) {
			return FindStoredDateResult.builder()
				.exhId(exhId)
				.visitDate(visitDate)
				.dates(dates)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindExhResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final LocalDate exhPeriodStart;
		private final LocalDate exhPeriodEnd;
		private final String poster;
		private final Boolean favoriteExh;
		private final String painter;
		private final Integer fee;
		private final String intro;
		private final String url;

		public static FindExhResult findByExh(ExhEntity exh, Boolean favoriteExh, String poster) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(poster)
				.favoriteExh(favoriteExh)
				.painter(exh.getPainter())
				.fee(exh.getFee())
				.intro(exh.getIntro())
				.url(exh.getUrl())
				.build();
		}

		public static FindExhResult findByExhForList(ExhEntity exh, Boolean favoriteExh, String poster) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(poster)
				.favoriteExh(favoriteExh)
				.build();
		}
	}
}
