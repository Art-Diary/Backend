package klieme.artdiary.exhibition.info;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoredListOfDate {
	private final Long exhVisitId;
	private final LocalDate visitDate;
}
