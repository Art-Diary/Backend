package klieme.artdiary.calendar.service;

import java.io.IOException;
import java.util.List;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.calendar.info.ScheduleInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface CalendarReadUseCase {
	List<FindCalendarResult> getExhSchedule(CalendarFindQuery query) throws IOException;

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class CalendarFindQuery {
		private final CalendarKind kind;
		private final Long gatherId;
		private final Integer year;
		private final Integer month;
	}

	@Getter
	@ToString
	@Builder
	class FindCalendarResult {
		private final Integer day;
		private final List<ScheduleInfo> scheduleInfoList;

		public static FindCalendarResult findByCalendar(Integer day, List<ScheduleInfo> scheduleInfoList) {
			return FindCalendarResult.builder()
				.day(day)
				.scheduleInfoList(scheduleInfoList)
				.build();
		}
	}
}
