package klieme.artdiary.exhibitions.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.SearchEntity;
import klieme.artdiary.exhibitions.data_access.repository.SearchRepository;

@Service
public class SearchService implements SearchOperationUseCase, SearchReadUseCase {
	private final SearchRepository searchRepository;

	@Autowired
	public SearchService(SearchRepository searchRepository) {
		this.searchRepository = searchRepository;
	}

	@Override
	@Transactional
	public void createSearchContent(SearchContentCreateCommand command) {

		Long userId = getUserId();//해당 유저 아이디

		// 이미 저장한 검색기록인지 확인
		Optional<SearchEntity> savedSearchContent = searchRepository.findBySearchNameAndUserId(
			command.getSearchContent(), userId);

		SearchEntity newSearchContent;
		if (savedSearchContent.isPresent()) {
			//있으면 시간만 업데이트 [수정]
			newSearchContent = savedSearchContent.get();
			newSearchContent.updateSearchEntity(command.getSearchTime());
		} else {
			// 없으면 저장
			newSearchContent = SearchEntity.builder()
				.searchName(command.getSearchContent())
				.userId(userId)
				.searchTime(command.getSearchTime())
				.build();
		}
		searchRepository.save(newSearchContent);

	}

	@Override
	public List<FindSearchResult> getSearchContents() throws IOException {

		List<FindSearchResult> results = new ArrayList<>();

		//userId로 해당 유저의 검색기록 가져오기
		List<SearchEntity> sEntities = searchRepository.findByUserId(getUserId());
		for (SearchEntity sEntity : sEntities) {
			results.add(SearchReadUseCase.FindSearchResult.findSearchName(sEntity));

		}
		return results;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}

}