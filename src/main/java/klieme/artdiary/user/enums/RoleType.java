package klieme.artdiary.user.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RoleType {
	USER("USER"),
	ADMIN("ADMIN");

	private final String label;

	RoleType(String label) {
		this.label = label;
	}

	public String label() {
		return this.label;
	}

	private static final Map<String, RoleType> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(RoleType::label, Function.identity()));

	public static RoleType valueOfLabel(String label) {
		return BY_LABEL.get(label);
	}
}
