package klieme.artdiary.exhibitions.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ExhField {
	PHOTO("사진"),
	PAINTING("회화"),
	PIECE("조각"),
	CRAFTS("공예"),
	MEDIA_ART("미디어아트"),
	OTHER("그외");

	private final String label;

	ExhField(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, ExhField> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(ExhField::label, Function.identity()));

	public static ExhField valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
