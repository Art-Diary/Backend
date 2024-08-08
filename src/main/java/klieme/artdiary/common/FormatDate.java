package klieme.artdiary.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class FormatDate {
	public static String changeDateFormat(LocalDate date) {
		if (date == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		return date.format(formatter);
	}

	public static String changeDateTimeFormat(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
		return dateTime.format(formatter);
	}
}
