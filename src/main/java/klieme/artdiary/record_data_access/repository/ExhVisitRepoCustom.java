package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;

public interface ExhVisitRepoCustom {
	List<Map<String, Object>> getMyVisitedDateListOfExh(Long userId, Long exhId);

	List<ExhVisitEntity> getGroupVisitedDateListOfExh(Long userId, Long groupId, Long exhId);

	Boolean checkExhVisitByExhVisitId(Long exhVisitId, Long userId, Long exhId);

	List<Map<String, Object>> getVisitInfoForCalendar(CalendarKind kind, Long userId, Long gatherId,
		LocalDate startDate, LocalDate endDate);

	List<Map<String, Object>> getVisitSoloDateForFcm();

	List<Map<String, Object>> getVisitGatheringDateForFcm();
}
