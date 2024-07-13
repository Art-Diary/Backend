package klieme.artdiary.solo.info;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoredDateInfo {
	// 삭제
	private final Long gatherExhId; // 개인일 경우엔 null
	private final Long userExhId; // 모임일 경우엔 null
	//
	private final Long exhVisitId;
	private final LocalDate visitDate;
}
