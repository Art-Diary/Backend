package klieme.artdiary.calendar.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CalendarKind {
	ALL("all"),
	ALONE("alone"),
	GATHER("gather");

	private final String label;

	CalendarKind(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, CalendarKind> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(CalendarKind::label, Function.identity()));

	public static CalendarKind valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
