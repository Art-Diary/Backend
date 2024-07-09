package klieme.artdiary.exhibitions.service;

import java.io.IOException;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.SearchEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface SearchReadUseCase {
	List<FindSearchResult> getSearchContents() throws IOException;

	@Getter
	@ToString
	@Builder
	class FindSearchResult {
		private String searchContent;

		public static FindSearchResult findSearchName(SearchEntity entity) {
			return FindSearchResult.builder().searchContent(entity.getSearchName()).build();
		}

	}
}
