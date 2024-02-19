package klieme.artdiary.gatherings.Info;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExhibitionInfo {
	private final Long exhId;
	private final String exhName;
	private final String poster;
	private final Double rate;
}
