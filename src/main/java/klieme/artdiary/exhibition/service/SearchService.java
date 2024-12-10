package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.SearchEntity;
import klieme.artdiary.exhibition.data_access.repository.SearchRepository;

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
			// 해당 유저의 검색 기록 수가 10개 이상인지 확인
			List<SearchEntity> sEntities = searchRepository.findByUserId(getUserId());

			// 10개 이상이면 검색시간 (searchTime)이 가장 오랜된 기록 삭제
			if (sEntities.size() >= 10) {
				//가장 오래된 순으로 정렬
				sEntities.sort(Comparator.comparing(SearchEntity::getSearchTime));
				//가장 오래된 기록 삭제
				SearchEntity deleteEntity = sEntities.getFirst();
				searchRepository.delete(deleteEntity);
			}

			// 새 검색단어 저장
			newSearchContent = SearchEntity.builder()
				.searchName(command.getSearchContent())
				.userId(userId)
				.searchTime(command.getSearchTime())
				.build();
		}
		searchRepository.save(newSearchContent);

	}

	@Override
	public List<FindSearchResult> getSearchContents() {

		List<FindSearchResult> results = new ArrayList<>();

		//userId로 해당 유저의 검색기록 가져오기
		List<SearchEntity> sEntities = searchRepository.findByUserId(getUserId());

		//가장 최근 시간순으로 정렬
		sEntities.sort((e1, e2) -> e2.getSearchTime().compareTo(e1.getSearchTime()));

		for (SearchEntity sEntity : sEntities) {
			results.add(SearchReadUseCase.FindSearchResult.findSearchContents(sEntity));

		}
		return results;
	}

	@Override
	@Transactional
	public void deleteSearchContent(Long searchId) {

		Long userId = getUserId();//해당 유저 아이디

		//삭제할 데이터 찾기
		SearchEntity entity = searchRepository.findBySearchIdAndUserId(
			searchId, userId).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		searchRepository.delete(entity);

	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

}
