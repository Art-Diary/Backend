package klieme.artdiary.solo.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyExhOperationUseCase {

	List<MyExhReadUseCase.FindMyStoredDateResult> addMyExhVisitDate(AddMyExhVisitDateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class AddMyExhVisitDateCommand {
		private final Long exhId;
		private final LocalDate visitDate;// 혜원 추가

	}
}
