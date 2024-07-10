package klieme.artdiary.exhibitions.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibitions.service.SearchReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchContentView {
	private final Long searchId;
	private final String searchContent;

	@Builder
	public SearchContentView(SearchReadUseCase.FindSearchResult result) {
		this.searchContent = result.getSearchContent();
		this.searchId = result.getSearchId();
	}
}
