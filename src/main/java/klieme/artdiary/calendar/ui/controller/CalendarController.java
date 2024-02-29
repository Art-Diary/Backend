package klieme.artdiary.calendar.ui.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.calendar.service.CalendarReadUseCase;
import klieme.artdiary.calendar.ui.view.CalendarView;
import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;

@RestController
@RequestMapping(value = "/calendars")
public class CalendarController {
	private final CalendarReadUseCase calendarReadUseCase;

	@Autowired
	public CalendarController(CalendarReadUseCase calendarReadUseCase) {
		this.calendarReadUseCase = calendarReadUseCase;
	}

	/**
	 * 모임과 날짜 별 저장된 전시회 조회
	 * "/calendars?kind=[all, alone, gather]&year=[]&month=[]&gatherId=[]"
	 */
	@GetMapping("")
	public ResponseEntity<List<CalendarView>> getExhSchedule(
		@RequestParam(name = "kind") String kind,
		@RequestParam(name = "gatherId", required = false) Long gatherId,
		@RequestParam(name = "year") Integer year,
		@RequestParam(name = "month") Integer month
	) throws IOException {
		// 요청 파라미터 검증
		if ((gatherId == null && CalendarKind.valueOfLabel(kind) == CalendarKind.GATHER)
			|| (gatherId != null && CalendarKind.valueOfLabel(kind) != CalendarKind.GATHER)) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// year와 month가 적절한 값이 들어왔는지 확인
		try {
			LocalDate targetDate = LocalDate.of(year, month, 1);
		} catch (Exception e) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// 파라미터로 받은 데이터 service로 전달하기 위함.
		var query = CalendarReadUseCase.CalendarFindQuery.builder()
			.kind(CalendarKind.valueOfLabel(kind))
			.gatherId(gatherId)
			.year(year)
			.month(month)
			.build();
		// 비즈니스 로직 호출
		List<CalendarReadUseCase.FindCalendarResult> results = calendarReadUseCase.getExhSchedule(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<CalendarView> viewList = new ArrayList<>();

		for (CalendarReadUseCase.FindCalendarResult result : results) {
			viewList.add(CalendarView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewList);
	}
}
