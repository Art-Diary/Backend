package klieme.artdiary.calendar.ui.view;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.calendar.service.CalendarReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final LocalDate exhPeriodStart;
	private final LocalDate exhPeriodEnd;
	private final String poster;
	private final LocalDate visitDate;
	private final Long gatherId; // 개인일 경우 null
	private final String gatherName; // 개인일 경우 null

	@Builder
	public CalendarView(CalendarReadUseCase.FindCalendarResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.exhPeriodStart = result.getExhPeriodStart();
		this.exhPeriodEnd = result.getExhPeriodEnd();
		this.poster = result.getPoster();
		this.visitDate = result.getVisitDate();
		this.gatherId = result.getGatherId();
		this.gatherName = result.getGatherName();
	}
}
