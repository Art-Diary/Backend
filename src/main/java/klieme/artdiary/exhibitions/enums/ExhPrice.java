package klieme.artdiary.exhibitions.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ExhPrice {
	FREE("무료"),
	PAY("유료"),
	UNDER20000("20000원 이하");

	private final String label;

	ExhPrice(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, ExhPrice> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(ExhPrice::label, Function.identity()));

	public static ExhPrice valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
