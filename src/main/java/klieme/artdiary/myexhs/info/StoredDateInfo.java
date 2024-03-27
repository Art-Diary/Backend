package klieme.artdiary.myexhs.info;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoredDateInfo {
	private final Long gatheringExhId; // 개인일 경우엔 null
	private final Long userExhId; // 모임일 경우엔 null
	private final LocalDate visitDate;
}
