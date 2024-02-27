package klieme.artdiary.calendar.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.calendar.info.ScheduleInfo;
import klieme.artdiary.calendar.service.CalendarReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarView {
	private final Integer day;
	private final List<ScheduleInfo> scheduleInfoList;

	@Builder
	public CalendarView(CalendarReadUseCase.FindCalendarResult result) {
		this.day = result.getDay();
		this.scheduleInfoList = result.getScheduleInfoList();
	}
}
