package klieme.artdiary.exhibition.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.exhibition.service.SearchOperationUseCase;
import klieme.artdiary.exhibition.service.SearchReadUseCase;
import klieme.artdiary.exhibition.ui.request_body.SearchContentsRequest;
import klieme.artdiary.exhibition.ui.view.SearchContentView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/search")
public class SearchController {

	private final SearchOperationUseCase searchOperationUseCase;
	private final SearchReadUseCase searchReadUseCase;

	@Autowired
	public SearchController(SearchOperationUseCase searchOperationUseCase,
		SearchReadUseCase searchReadUseCase) {
		this.searchOperationUseCase = searchOperationUseCase;
		this.searchReadUseCase = searchReadUseCase;
	}

	@PostMapping("")
	public void storeSearchContents(@Valid @RequestBody SearchContentsRequest request) {
		log.info("[전시회 검색기록 저장]");

		var command = SearchOperationUseCase.SearchContentCreateCommand.builder()
			.searchContent(request.getSearchContent())
			.searchTime(request.getSearchTime())
			.build();

		// 비즈니스 로직 호출
		searchOperationUseCase.createSearchContent(command);

	}

	@GetMapping("")
	public ResponseEntity<List<SearchContentView>> getSearchContentlist() throws IOException {
		log.info("[전시회 검색 기록 조회]");

		List<SearchReadUseCase.FindSearchResult> results = searchReadUseCase.getSearchContents();

		List<SearchContentView> viewResult = new ArrayList<>();
		for (SearchReadUseCase.FindSearchResult result : results) {
			viewResult.add(SearchContentView.builder().result(result).build());
		}

		return ResponseEntity.ok(viewResult);
	}

	@DeleteMapping("/{searchId}")
	public void deleteSearchContent(@PathVariable(name = "searchId") Long searchId) {
		log.info("[전시회 검색 기록 삭제]");

		//비즈니스 로직 호출
		searchOperationUseCase.deleteSearchContent(searchId);

	}

}
