package klieme.artdiary.exhibitions.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum ExhState {
	BEFORE_START("예정"),
	PROCEED("진행중"),
	END("종료");

	private final String label;

	ExhState(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, ExhState> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(ExhState::label, Function.identity()));

	public static ExhState valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
