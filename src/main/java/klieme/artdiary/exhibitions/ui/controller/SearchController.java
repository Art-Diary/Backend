package klieme.artdiary.exhibitions.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.exhibitions.service.SearchReadUseCase;
import klieme.artdiary.exhibitions.ui.request_body.SearchContentsRequest;
import klieme.artdiary.exhibitions.service.SearchOperationUseCase;
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

	// @GetMapping("")
	// public ResponseEntity<SearchContentView> getSearchContents{
	// 	//@PathVariable(name = "exhId") Long exhId) throws
	// 	//IOException {
	// 	log.info("[전시회 검색 기록 조회]");
	//
	//
	// 	var query= SearchReadUseCase.FindSearchResult.builder()
	// 		.searchContents(searhContents)
	// 		.build();
	// 	SearchReadUseCase.FindSearchResult result=searchReadUseCase.getSearchContents(query);
	//
	// 	return ResponseEntity.ok(SearchContentsView.builder().result(result).build());
	//
	// }

	// @GetMapping("/{exhId}/date") // ResponseEntity<>
	// public ResponseEntity<StoredDateView> getStoredDateOfExhs(
	// 	@PathVariable(name = "exhId") Long exhId,
	// 	@RequestParam(name = "gatherId", required = false) Long gatherId
	// ) {
	// 	log.info("[한 전시회에 대해 캘린더에 저장된 날짜 조회]");
	// 	var query = ExhReadUseCase.StoredDateFindQuery.builder()
	// 		.exhId(exhId)
	// 		.gatherId(gatherId)
	// 		.build();
	// 	ExhReadUseCase.FindStoredDateResult result = exhReadUseCase.getStoredDateOfExhs(query);
	//
	// 	return ResponseEntity.ok(StoredDateView.builder().result(result).build());
	// }
}
