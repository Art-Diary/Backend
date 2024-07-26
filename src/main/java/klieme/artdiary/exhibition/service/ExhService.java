package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.image.ImageTransfer;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import klieme.artdiary.exhibition.info.StoredListOfDate;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhId;
import klieme.artdiary.favoriteexh.data_access.repository.FavoriteExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMateId;
import klieme.artdiary.gathering.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepoCustom;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepoCustom;
import klieme.artdiary.record_data_access.repository.ExhVisitRepoCustomImpl;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.solo.data_access.entity.MydiaryEntity;
import klieme.artdiary.solo.data_access.entity.UserExhEntity;
import klieme.artdiary.solo.data_access.repository.MydiaryRepository;
import klieme.artdiary.solo.data_access.repository.UserExhRepository;
import klieme.artdiary.solo.info.StoredDateInfo;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class ExhService implements ExhOperationUseCase, ExhReadUseCase {

	private final ExhRepository exhRepository;
	private final UserExhRepository userExhRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final GatheringRepository gatheringRepository;
	private final FavoriteExhRepository favoriteExhRepository;
	private final MydiaryRepository mydiaryRepository;
	private final UserRepository userRepository;
	private final ImageTransfer imageTransfer;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;

	@Autowired
	public ExhService(ExhRepository exhRepository, UserExhRepository userExhRepository,
		GatheringMateRepository gatheringMateRepository, GatheringExhRepository gatheringExhRepository,
		GatheringDiaryRepository gatheringDiaryRepository, GatheringRepository gatheringRepository,
		FavoriteExhRepository favoriteExhRepository, MydiaryRepository mydiaryRepository,
		UserRepository userRepository, ImageTransfer imageTransfer, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository) {
		this.exhRepository = exhRepository;
		this.userExhRepository = userExhRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.gatheringRepository = gatheringRepository;
		this.favoriteExhRepository = favoriteExhRepository;
		this.mydiaryRepository = mydiaryRepository;
		this.userRepository = userRepository;
		this.imageTransfer = imageTransfer;
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

	/*
		@Transactional
		@Override
		public FindStoredDateResult addSoloExhCreateDummy(ExhOperationUseCase.AddSoloExhDummyCreateCommand command) {
			// 전시회 아이디 검증
			ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

			// 관람날짜 검증
			Optional<UserExhEntity> userExhEntity = userExhRepository.findByUserIdAndExhIdAndVisitDate(getUserId(),
				command.getExhId(), command.getVisitDate());

			if (userExhEntity.isPresent()) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			}

			// 전시회 일정에 맞춰 갈 수 있는지 확인
			if (exhEntity.getExhPeriodStart().isAfter(command.getVisitDate())
				|| exhEntity.getExhPeriodEnd().isBefore(command.getVisitDate())) {
				throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
			}

			// DB에 데이터 생성
			UserExhEntity entity = UserExhEntity.builder()
				.visitDate(command.getVisitDate())
				.userId(getUserId())
				.exhId(command.getExhId())
				.build();
			userExhRepository.save(entity);
			return FindStoredDateResult.findByStoredDate(command.getExhId(), command.getVisitDate(), null);
		}
	*/

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
	public List<FindExhResult> getExhList(ExhListFindQuery query) throws IOException {
		List<FindExhResult> results = new ArrayList<>();
		List<ExhEntity> exhEntityList = exhRepository.searchExhList(query.getSearchName(), query.getFieldList(),
			query.getPrice(), query.getStateList(), query.getDate());

		for (ExhEntity exh : exhEntityList) {
			results.add(getFindExhResult(exh));
		}
		return results;
	}

	@Override
	public ExhReadUseCase.FindExhResult getExhDetailInfo(Long exhId) throws
		IOException { //나중에 getfindexhresult함수 사용으로 바꿔보기

		ExhEntity entity = exhRepository.findByExhId(exhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		Optional<FavoriteExhEntity> favoriteExh = favoriteExhRepository.findByFavoriteExhId(FavoriteExhId.builder()
			.userId(getUserId())
			.exhId(exhId)
			.build());
		boolean isFavoriteExh = favoriteExh.isPresent();
		return FindExhResult.findByExh(entity, isFavoriteExh, imageTransfer.downloadImage(entity.getPoster()));
	}

	//[here/hw]
	@Override
	public List<FindDiaryResult> getAllOfExhIdDiaries(Long exhId) throws IOException {

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
		return getCurrentUserId();
	}

	private Boolean checkField(ExhField field, ExhEntity exh) {
		if (field == null) {
			return true;
		}
		if (field == ExhField.OTHER) {
			if (exh.getArt() == null) { // other && art == null
				return true;
			} else { // other && art != null
				return !exh.getArt().contains(ExhField.PHOTO.label())
					&& !exh.getArt().contains(ExhField.PAINTING.label())
					&& !exh.getArt().contains(ExhField.PIECE.label())
					&& !exh.getArt().contains(ExhField.CRAFTS.label())
					&& !exh.getArt().contains(ExhField.MEDIA_ART.label());
			}
		} else {
			if (exh.getArt() == null) { // !other && art == null
				return false;
			} else { // !other && art != null
				return exh.getArt().contains(field.label());
			}
		}
	}

	private Boolean checkPrice(ExhPrice price, ExhEntity exh) {
		if (price == null) {
			return true;
		}
		switch (price) {
			case ExhPrice.FREE:
				if (exh.getFee() == 0) {
					return true;
				}
				break;
			case ExhPrice.PAY:
				if (exh.getFee() != 0) {
					return true;
				}
				break;
			default:
				if (exh.getFee() <= 20000) {
					return true;
				}
		}
		return false;
	}

	private Boolean checkState(ExhState state, ExhEntity exh) {
		if (state == null) {
			return true;
		}
		LocalDate now = LocalDate.now();
		switch (state) {
			case ExhState.BEFORE_START:
				if (exh.getExhPeriodStart().isAfter(now)) {
					return true;
				}
				break;
			case ExhState.END:
				if (exh.getExhPeriodEnd().isBefore(now)) {
					return true;
				}
				break;
			default:
				if (isProceedExh(exh, now)) {
					return true;
				}
		}
		return false;
	}

	private Boolean isProceedExh(ExhEntity exh, LocalDate targetDate) {
		return exh.getExhPeriodStart().isEqual(targetDate) || exh.getExhPeriodEnd().isEqual(targetDate)
			|| (exh.getExhPeriodStart().isBefore(targetDate) && exh.getExhPeriodEnd().isAfter(targetDate));
	}

	private FindExhResult getFindExhResult(ExhEntity exh) throws IOException {
		// 전시회 좋아요 여부 구현
		Optional<FavoriteExhEntity> favoriteExh = favoriteExhRepository.findByFavoriteExhId(FavoriteExhId.builder()
			.userId(getUserId())
			.exhId(exh.getExhId())
			.build());
		boolean isFavoriteExh = favoriteExh.isPresent();
		String thumbnail = imageTransfer.downloadImage(exh.getPoster());
		return FindExhResult.findByExhForList(exh, isFavoriteExh, thumbnail);
	}
}
