package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import klieme.artdiary.calendar.enums.CalendarKind;

public interface ExhVisitRepoCustom {
	List<Map<String, Object>> getMyVisitedDateListOfExh(Long userId, Long exhId);

	List<Map<String, Object>> getGroupVisitedDateListOfExh(Long userId, Long groupId, Long exhId);

	Boolean checkExhVisitByExhVisitId(Long exhVisitId, Long userId, Long exhId);

	List<Map<String, Object>> getVisitInfoForCalendar(CalendarKind kind, Long userId, Long gatherId,
		LocalDate startDate, LocalDate endDate);
}
