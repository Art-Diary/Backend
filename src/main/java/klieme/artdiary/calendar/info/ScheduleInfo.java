package klieme.artdiary.calendar.info;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduleInfo {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String exhPeriodStart;
	private final String exhPeriodEnd;
	private final String poster;
	private final String visitDate;
	private final Long gatherId; // 개인일 경우 null
	private final String gatherName; // 개인일 경우 null
	private final Long exhVisitId;
}
