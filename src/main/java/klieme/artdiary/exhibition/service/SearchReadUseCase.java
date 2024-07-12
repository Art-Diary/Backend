package klieme.artdiary.exhibition.service;

import java.io.IOException;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.SearchEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface SearchReadUseCase {
	List<FindSearchResult> getSearchContents() throws IOException;

	@Getter
	@ToString
	@Builder
	class FindSearchResult {
		private Long searchId;
		private String searchContent;

		public static FindSearchResult findSearchContents(SearchEntity entity) {
			return FindSearchResult.builder()
				.searchId(entity.getSearchId())
				.searchContent(entity.getSearchName())
				.build();
		}

	}
}
