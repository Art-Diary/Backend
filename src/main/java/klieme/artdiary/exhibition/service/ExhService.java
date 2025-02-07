package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.image.ImageType;
import klieme.artdiary.common.image.S3ImageTransfer;
import klieme.artdiary.exhibition.data_access.entity.CategoryEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkId;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.CategoryRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhCategoryLinkRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.exhibition.info.StoredListOfDate;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;

@Service
public class ExhService implements ExhOperationUseCase, ExhReadUseCase {

	private final ExhRepository exhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final GatheringRepository gatheringRepository;
	private final CategoryRepository categoryRepository;
	private final ExhCategoryLinkRepository exhCategoryLinkRepository;
	private final S3ImageTransfer s3ImageTransfer;

	@Autowired
	public ExhService(ExhRepository exhRepository, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository, GatheringRepository gatheringRepository, CategoryRepository categoryRepository,
		ExhCategoryLinkRepository exhCategoryLinkRepository, S3ImageTransfer s3ImageTransfer) {
		this.exhRepository = exhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.gatheringRepository = gatheringRepository;
		this.categoryRepository = categoryRepository;
		this.exhCategoryLinkRepository = exhCategoryLinkRepository;
		this.s3ImageTransfer = s3ImageTransfer;
	}

	//[here/hw]
	@Override
	public FindStoredDateResult getStoredDateOfExhsByGatherId(StoredDateFindQuery query) {

		//NEW getStoredDateOfExhs
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<StoredListOfDate> dateList = new ArrayList<>();
		List<ExhVisitEntity> entities;
		GatheringEntity gathering = null;

		// 전시회 아이디 검증
		exhRepository.findByExhId(query.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (query.getGatherId() == null) {
			//혼자 다녀온 전시회이면 ExhVisit 테이블에서 날짜리스트 가져오기
			entities = exhVisitRepository.findByUserIdAndExhId(userId,
				query.getExhId());

		} else {
			//그룹에서 다녀온 전시회
			gathering = gatheringRepository.findByGatherId(query.getGatherId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			entities = exhVisitRepository.getGroupVisitedDateListOfExh(userId,
				query.getGatherId(),
				query.getExhId());
		}
		for (ExhVisitEntity entity : entities) {
			if (entity.getVisitDate() == null) { // 날짜 모름일 때는 건너뜀.
				continue;
			}
			dateList.add(StoredListOfDate.builder()
				.exhVisitId(entity.getExhVisitId())
				.visitDate(changeDateFormat(entity.getVisitDate()))
				.build());

		}
		return FindStoredDateResult.findByStoredDate(query.getExhId(), gathering, dateList);

	}

	@Override
	public List<FindExhResult> getExhList(ExhListFindQuery query) {
		/* api 요청 옵션에 전시회 진행 상황이 포함되어있으면 해당 진행 상황 적용.
		 * 옵션에 진행 상황이 없으면 "현재 진행 중"인 전시회 적용.
		 */
		/* 전시회 리스트 고정 순서
		 * 1. 좋아요 많은 순
		 * 2. 최근에 시작한 순
		 * */
		List<FindExhResult> results = new ArrayList<>();
		List<Map<String, Object>> infoList = exhRepository.searchExhList(query.getSearchName(), query.getFieldList(),
			query.getPrice(), query.getStateList(), query.getDate(), getUserId());

		for (Map<String, Object> info : infoList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			Integer haveFavoriteByUser = (Integer)info.get("haveFavoriteByUser");

			results.add(FindExhResult.findByExhForList(exhibition, haveFavoriteByUser == 1));
		}
		return results;
	}

	@Override
	public List<FindExhResult> getExhListBySearchName(String searchName) {

		List<FindExhResult> results = new ArrayList<>();
		List<Map<String, Object>> infoList = exhRepository.searchExhListBySearchName(searchName, getUserId());

		for (Map<String, Object> info : infoList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			Integer haveFavoriteByUser = (Integer)info.get("haveFavoriteByUser");

			results.add(FindExhResult.findByExhForList(exhibition, haveFavoriteByUser == 1));
		}
		return results;
	}

	@Override
	public ExhReadUseCase.FindExhResult getExhDetailInfo(Long exhId) {
		Map<String, Object> exhDetailInfo = exhRepository.getExhDetailInfo(getUserId(), exhId);
		ExhEntity exh = (ExhEntity)exhDetailInfo.get("exhibition");
		String category = (String)exhDetailInfo.get("category");
		Boolean isFavoriteExh = (Boolean)exhDetailInfo.get("haveFavoriteByUser");

		return FindExhResult.findByExh(exh, isFavoriteExh, category);
	}

	//[here/hw]
	@Override
	public List<FindDiaryResult> getAllOfExhIdDiaries(Long exhId) {

		List<FindDiaryResult> results = new ArrayList<>();
		List<Map<String, Object>> diaryList = diaryRepository.getAllOfDiaries(getUserId(), exhId);

		for (Map<String, Object> item : diaryList) {
			DiaryEntity diary = (DiaryEntity)item.get("diaryEntity");
			ExhVisitEntity exhVisit = (ExhVisitEntity)item.get("exhVisitEntity");
			GatheringEntity gathering = (GatheringEntity)item.get("gatheringEntity");
			UserEntity user = (UserEntity)item.get("userEntity");
			ExhEntity exh = (ExhEntity)item.get("exhEntity");

			results.add(FindDiaryResult.findStoredDiary(diary, exhVisit, user, exh, gathering));
		}
		return results;
	}

	@Transactional
	@Override
	public FindExhResult updateExhDetailInfo(ExhUpdateCommand command) {
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		String uploadImageUrl = null;

		if (command.getPoster() != null && !Objects.equals(command.getPoster().getOriginalFilename(),
			exhEntity.getPoster())) {
			uploadImageUrl = s3ImageTransfer.uploadImageToStorage(
				S3ImageTransfer.UploadQuery.builder()
					.type(ImageType.REG_EXH)
					.image(command.getPoster())
					.exhId(command.getExhId())
					.build());
		}
		// 카테고리
		List<CategoryEntity> categoryList = categoryRepository.findAll();
		List<ExhCategoryLinkEntity> oldList = exhCategoryLinkRepository.findByExhCategoryLinkIdExhId(
			command.getExhId());
		List<String> fieldList = new ArrayList<>(Arrays.asList(command.getArt().split(","))); // 수정된 카테고리

		// 삭제도 필요
		for (ExhCategoryLinkEntity old : oldList) {
			Long categoryId = old.getExhCategoryLinkId().getCategoryId();
			// 여전히 있는지 확인
			CategoryEntity category = categoryList.stream()
				.filter(c -> categoryId.equals(c.getCategoryId()))
				.findAny()
				.orElse(null);
			if (category == null) {
				continue;
			}
			// -> 없으면 삭제
			if (!fieldList.contains(category.getName())) {
				exhCategoryLinkRepository.delete(old);
			}
			// -> 있으면 그냥 넘기기
		}
		// 추가
		for (String field : fieldList) {
			CategoryEntity category = categoryList.stream()
				.filter(c -> field.equals(c.getName()))
				.findAny()
				.orElse(null);

			if (category == null) {
				continue;
			}
			exhCategoryLinkRepository.save(ExhCategoryLinkEntity.builder()
				.exhCategoryLinkId(ExhCategoryLinkId.builder()
					.exhId(command.getExhId())
					.categoryId(category.getCategoryId())
					.build())
				.build());
		}
		ExhEntity updatedExh = ExhEntity.builder()
			.exhName(command.getExhName())
			.gallery(command.getGallery())
			.exhPeriodStart(command.getExhPeriodStart())
			.exhPeriodEnd(command.getExhPeriodEnd())
			.painter(command.getPainter())
			.fee(command.getFee())
			.intro(command.getIntro())
			.url(command.getUrl())
			.poster(uploadImageUrl)
			.source(command.getSource())
			.build();

		exhEntity.updateExhEntity(updatedExh);
		exhRepository.save(exhEntity);
		return FindExhResult.findByExh(exhEntity, null, command.getArt());
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}
