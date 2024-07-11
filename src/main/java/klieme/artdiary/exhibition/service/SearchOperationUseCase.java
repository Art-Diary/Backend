package klieme.artdiary.exhibition.service;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SearchOperationUseCase {

	void createSearchContent(
		klieme.artdiary.exhibition.service.SearchOperationUseCase.SearchContentCreateCommand command);

	void deleteSearchContent(Long searchId);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class SearchContentCreateCommand {
		private final String searchContent;
		private final LocalDateTime searchTime;
	}
}
