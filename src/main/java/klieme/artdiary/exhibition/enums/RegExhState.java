package klieme.artdiary.exhibition.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RegExhState {
	COMPLETE("완료"),
	WAIT("대기"),
	FAIL("실패");

	private final String label;

	RegExhState(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, RegExhState> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(RegExhState::label, Function.identity()));

	public static RegExhState valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
