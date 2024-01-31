package klieme.artdiary.exhibitions.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ExhReadUseCase {
	FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query);

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
}
