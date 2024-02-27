package klieme.artdiary.calendar.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface CalendarReadUseCase {
	List<FindCalendarResult> getExhSchedule(CalendarFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class CalendarFindQuery {
		private final CalendarKind kind;
		private final Long gatherId;
		private final LocalDate date;
	}

	@Getter
	@ToString
	@Builder
	class FindCalendarResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final LocalDate exhPeriodStart;
		private final LocalDate exhPeriodEnd;
		private final String poster;
		private final LocalDate visitDate;
		private final Long gatherId; // 개인일 경우 null
		private final String gatherName; // 개인일 경우 null

		public static FindCalendarResult findByCalendar(ExhEntity exh, LocalDate visitDate, String poster,
			GatheringEntity gathering) {
			return FindCalendarResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(poster)
				.visitDate(visitDate)
				.gatherId(gathering.getGatherId())
				.gatherName(gathering.getGatherName())
				.build();
		}
	}
}
