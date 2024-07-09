package klieme.artdiary.exhibitions.service;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SearchOperationUseCase {

	void createSearchContent(
		klieme.artdiary.exhibitions.service.SearchOperationUseCase.SearchContentCreateCommand command);

	void deleteSearchContent(
		klieme.artdiary.exhibitions.service.SearchOperationUseCase.SearchContentCreateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class SearchContentCreateCommand {
		private final String searchContent;
		private final LocalDateTime searchTime;
	}
}
