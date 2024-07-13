package klieme.artdiary.solo.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyExhOperationUseCase {

	List<MyExhReadUseCase.FindMyStoredDateResult> addMyExhVisitDateDummy(AddMyExhVisitDateDummyCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class AddMyExhVisitDateDummyCommand {
		private final Long exhId;
		//private final Long userExhId; //혜원 추가
		private final LocalDate visitDate;// 혜원 추가

	}
}
