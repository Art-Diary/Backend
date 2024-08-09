package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.exhibition.info.StoredListOfDate;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhId;
import klieme.artdiary.favoriteexh.data_access.repository.FavoriteExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;

@Service
public class ExhService implements ExhOperationUseCase, ExhReadUseCase {

	private final ExhRepository exhRepository;
	private final FavoriteExhRepository favoriteExhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;

	@Autowired
	public ExhService(ExhRepository exhRepository, FavoriteExhRepository favoriteExhRepository,
		ExhVisitRepository exhVisitRepository, DiaryRepository diaryRepository) {
		this.exhRepository = exhRepository;
		this.favoriteExhRepository = favoriteExhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
	}

	@Transactional
	@Override
	public String createDummy(ExhOperationUseCase.ExhDummyCreateCommand command) {
		ExhEntity entity = ExhEntity.builder()
			.exhName(command.getExhName())
			.gallery(command.getGallery())
			.exhPeriodStart(command.getExhPeriodStart())
			.exhPeriodEnd(command.getExhPeriodEnd())
			.painter(command.getPainter())
			.fee(command.getFee())
			.intro(command.getIntro())
			.url(command.getUrl())
			.poster(command.getPoster())
			.build();
		exhRepository.save(entity);
		return "complete";
	}

	//[here/hw]
	@Override
	public FindStoredDateResult getStoredDateOfExhsByGatherId(StoredDateFindQuery query) {

		//NEW getStoredDateOfExhs
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<StoredListOfDate> dateList = new ArrayList<>();
		List<ExhVisitEntity> entities;

		// 전시회 아이디 검증
		exhRepository.findByExhId(query.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (query.getGatherId() == null) {
			//혼자 다녀온 전시회이면 ExhVisit 테이블에서 날짜리스트 가져오기
			entities = exhVisitRepository.findByUserIdAndExhId(userId,
				query.getExhId());

		} else {
			//그룹에서 다녀온 전시회
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
		return FindStoredDateResult.findByStoredDate(query.getExhId(), dateList);

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
	public ExhReadUseCase.FindExhResult getExhDetailInfo(Long exhId) { //나중에 getfindexhresult함수 사용으로 바꿔보기

		ExhEntity entity = exhRepository.findByExhId(exhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		Optional<FavoriteExhEntity> favoriteExh = favoriteExhRepository.findByFavoriteExhId(FavoriteExhId.builder()
			.userId(getUserId())
			.exhId(exhId)
			.build());
		boolean isFavoriteExh = favoriteExh.isPresent();
		return FindExhResult.findByExh(entity, isFavoriteExh);
	}

	//[here/hw]
	@Override
	public List<FindDiaryResult> getAllOfExhIdDiaries(Long exhId) {

		List<FindDiaryResult> results = new ArrayList<>();
		List<Map<String, Object>> diaryList = null;
		diaryList = diaryRepository.getAllOfDiaries(getUserId(), exhId);

		for (Map<String, Object> item : diaryList) {
			DiaryEntity diary = (DiaryEntity)item.get("diaryEntity");
			ExhVisitEntity exhVisit = (ExhVisitEntity)item.get("exhVisitEntity");
			GatheringEntity gathering = (GatheringEntity)item.get("gatheringEntity");
			UserEntity user = (UserEntity)item.get("userEntity");
			ExhEntity exh = (ExhEntity)item.get("exhEntity");

			if (gathering == null) {//개인이 다녀온 기록: groupId==null인 경우

				if (user == null) {// 유저가 탈퇴하여 userId가 null인 경우 고려
					results.add(FindDiaryResult.findStoredAnonymousDiary(diary, exhVisit,
						exh));
				} else {
					results.add(FindDiaryResult.findStoredSoloDiary(diary, exhVisit, user,
						exh));
				}
			} else {//그룹에서 다녀온 기록: userId==null인 경우

				if (user == null) {// 유저가 탈퇴하여 userId가 null인 경우 고려
					results.add(FindDiaryResult.findStoredAnonymousDiary(diary, exhVisit,
						exh));
				} else {
					results.add(FindDiaryResult.findStoredGroupDiary(diary, exhVisit, user,
						exh, gathering));
				}
			}

		}

		return results;
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	// private Boolean checkField(ExhField field, ExhEntity exh) {
	// 	if (field == null) {
	// 		return true;
	// 	}
	// 	if (field == ExhField.OTHER) {
	// 		if (exh.getArt() == null) { // other && art == null
	// 			return true;
	// 		} else { // other && art != null
	// 			return !exh.getArt().contains(ExhField.PHOTO.label())
	// 				&& !exh.getArt().contains(ExhField.PAINTING.label())
	// 				&& !exh.getArt().contains(ExhField.PIECE.label())
	// 				&& !exh.getArt().contains(ExhField.CRAFTS.label())
	// 				&& !exh.getArt().contains(ExhField.MEDIA_ART.label());
	// 		}
	// 	} else {
	// 		if (exh.getArt() == null) { // !other && art == null
	// 			return false;
	// 		} else { // !other && art != null
	// 			return exh.getArt().contains(field.label());
	// 		}
	// 	}
	// }
	//
	// private Boolean checkPrice(ExhPrice price, ExhEntity exh) {
	// 	if (price == null) {
	// 		return true;
	// 	}
	// 	switch (price) {
	// 		case ExhPrice.FREE:
	// 			if (exh.getFee() == 0) {
	// 				return true;
	// 			}
	// 			break;
	// 		case ExhPrice.PAY:
	// 			if (exh.getFee() != 0) {
	// 				return true;
	// 			}
	// 			break;
	// 		default:
	// 			if (exh.getFee() <= 20000) {
	// 				return true;
	// 			}
	// 	}
	// 	return false;
	// }
	//
	// private Boolean checkState(ExhState state, ExhEntity exh) {
	// 	if (state == null) {
	// 		return true;
	// 	}
	// 	LocalDate now = LocalDate.now();
	// 	switch (state) {
	// 		case ExhState.BEFORE_START:
	// 			if (exh.getExhPeriodStart().isAfter(now)) {
	// 				return true;
	// 			}
	// 			break;
	// 		case ExhState.END:
	// 			if (exh.getExhPeriodEnd().isBefore(now)) {
	// 				return true;
	// 			}
	// 			break;
	// 		default:
	// 			if (isProceedExh(exh, now)) {
	// 				return true;
	// 			}
	// 	}
	// 	return false;
	// }
	//
	// private Boolean isProceedExh(ExhEntity exh, LocalDate targetDate) {
	// 	return exh.getExhPeriodStart().isEqual(targetDate) || exh.getExhPeriodEnd().isEqual(targetDate)
	// 		|| (exh.getExhPeriodStart().isBefore(targetDate) && exh.getExhPeriodEnd().isAfter(targetDate));
	// }
}
