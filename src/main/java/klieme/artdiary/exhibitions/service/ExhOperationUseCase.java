package klieme.artdiary.exhibitions.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

public interface ExhOperationUseCase {

	String createDummy(klieme.artdiary.exhibitions.service.ExhOperationUseCase.ExhDummyCreateCommand command);

	/*ExhReadUseCase.FindStoredDateResult addSoloExhCreateDummy(
		klieme.artdiary.exhibitions.service.ExhOperationUseCase.AddSoloExhDummyCreateCommand command);
*/
	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class ExhDummyCreateCommand {
		private final String exhName;
		private final String gallery;
		private final LocalDate exhPeriodStart;
		private final LocalDate exhPeriodEnd;
		private final String painter;
		private final Integer fee;
		private final String intro;
		private final String url;
		private final String poster;

	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class AddSoloExhDummyCreateCommand {
		private final LocalDate visitDate;
		private final Long exhId;

	}
}
