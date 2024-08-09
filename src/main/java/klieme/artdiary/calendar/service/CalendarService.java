package klieme.artdiary.calendar.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.calendar.info.ScheduleInfo;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;

@Service
public class CalendarService implements CalendarReadUseCase {
	private final ExhVisitRepository exhVisitRepository;

	@Autowired
	public CalendarService(ExhVisitRepository exhVisitRepository) {
		this.exhVisitRepository = exhVisitRepository;
	}

	@Override
	public List<FindCalendarResult> getExhSchedule(CalendarFindQuery query) {
		// 반환 리스트
		List<FindCalendarResult> results = new ArrayList<>();
		// FindCalendarTestResult의 dayOfScheduleInfos 값 구하기
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos = new HashMap<>();
		// 날짜 비교 중 월 시작 날짜와 마지막 날짜 구하기
		LocalDate selectedStartDate = LocalDate.of(query.getYear(), query.getMonth(), 1);
		LocalDate selectedEndDate = selectedStartDate.withDayOfMonth(selectedStartDate.lengthOfMonth());
		List<Map<String, Object>> visitInfo;

		if (query.getKind() == CalendarKind.ALONE) { // 개인일 경우
			visitInfo = exhVisitRepository.getVisitInfoForCalendar(CalendarKind.ALONE, getUserId(), null,
				selectedStartDate,
				selectedEndDate);
		} else if (query.getKind() == CalendarKind.GATHER) { // 모임일 경우
			visitInfo = exhVisitRepository.getVisitInfoForCalendar(CalendarKind.GATHER, getUserId(),
				query.getGatherId(), selectedStartDate, selectedEndDate);
		} else { // 전체일 경우
			visitInfo = exhVisitRepository.getVisitInfoForCalendar(CalendarKind.ALL, getUserId(), null,
				selectedStartDate, selectedEndDate);
		}
		dayOfVisitInfo(visitInfo, dayOfScheduleInfos);
		for (int day = 1; day <= selectedEndDate.getDayOfMonth(); day++) {
			if (dayOfScheduleInfos.get(day) != null) {
				results.add(FindCalendarResult.findByCalendar(day, dayOfScheduleInfos.get(day)));
			} else {
				results.add(FindCalendarResult.findByCalendar(day, null));
			}
		}
		return results;
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private void dayOfVisitInfo(List<Map<String, Object>> visitInfo,
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos) {
		for (Map<String, Object> info : visitInfo) {
			ExhVisitEntity exhVisit = (ExhVisitEntity)info.get("exhVisit");
			GatheringEntity gathering = (GatheringEntity)info.get("gathering");
			ExhEntity exh = (ExhEntity)info.get("exhibition");

			// 날짜 별 전시회 추가
			int day = exhVisit.getVisitDate().getDayOfMonth();

			dayOfScheduleInfos.computeIfAbsent(day, k -> new ArrayList<>());
			dayOfScheduleInfos.get(day).add(ScheduleInfo.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(changeDateFormat(exh.getExhPeriodStart()))
				.exhPeriodEnd(changeDateFormat(exh.getExhPeriodEnd()))
				.poster(exh.getPoster())
				.visitDate(changeDateFormat(exhVisit.getVisitDate()))
				.exhVisitId(exhVisit.getExhVisitId())
				.gatherId(gathering != null ? gathering.getGatherId() : null)
				.gatherName(gathering != null ? gathering.getGatherName() : null)
				.build());
		}
	}
}
