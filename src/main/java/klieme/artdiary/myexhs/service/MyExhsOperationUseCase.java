package klieme.artdiary.myexhs.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyExhsOperationUseCase {

	List<MyExhsReadUseCase.FindMyStoredDateResult> addMyExhVisitDateDummy(AddMyExhVisitDateDummyCommand command);

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
