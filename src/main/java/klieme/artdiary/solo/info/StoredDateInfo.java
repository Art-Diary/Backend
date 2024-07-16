package klieme.artdiary.solo.info;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoredDateInfo {
	private final Long exhVisitId;
	private final String visitDate;
}
