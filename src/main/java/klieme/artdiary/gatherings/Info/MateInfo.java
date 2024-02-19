package klieme.artdiary.gatherings.Info;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MateInfo {
	private final Long userId;
	private final String nickname;
}
