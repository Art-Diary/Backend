package klieme.artdiary.common.image;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ImageType {
	PROFILE("프로필"),
	THUMBNAIL("기록 썸네일"),
	REG_EXH("전시회 등록");

	private final String label;

	ImageType(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, ImageType> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(ImageType::label, Function.identity()));

	public static ImageType valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
