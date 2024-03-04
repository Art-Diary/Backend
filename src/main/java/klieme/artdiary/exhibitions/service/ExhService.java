package klieme.artdiary.exhibitions.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.exhibitions.enums.ExhField;
import klieme.artdiary.exhibitions.enums.ExhPrice;
import klieme.artdiary.exhibitions.enums.ExhState;
import klieme.artdiary.favoriteexhs.data_access.entity.FavoriteExhEntity;
import klieme.artdiary.favoriteexhs.data_access.entity.FavoriteExhId;
import klieme.artdiary.favoriteexhs.data_access.repository.FavoriteExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

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

	@Autowired
	public ExhService(ExhRepository exhRepository, UserExhRepository userExhRepository,
		GatheringMateRepository gatheringMateRepository, GatheringExhRepository gatheringExhRepository,
		GatheringDiaryRepository gatheringDiaryRepository, GatheringRepository gatheringRepository,
		FavoriteExhRepository favoriteExhRepository, MydiaryRepository mydiaryRepository,
		UserRepository userRepository, ImageTransfer imageTransfer) {
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
	@Override
	public FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query) {
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<LocalDate> dates = new ArrayList<>();

		// 전시회 아이디 검증
		exhRepository.findByExhId(query.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (query.getGatherId() == null) {
			// (목적) 한 전시회에 대한 캘린더에 저장된 개인의 일정 날짜 조회 로직 구현
			List<UserExhEntity> entities = userExhRepository.findByUserIdAndExhId(userId, query.getExhId());
			for (UserExhEntity entity : entities) {
				if (entity.getVisitDate() == null) { // 날짜 모름일 때는 건너뜀.
					continue;
				}
				dates.add(entity.getVisitDate());
			}
		} else {
			// (목적) 한 전시회에 대한 캘린더에 저장된 특정 모임의 일정 날짜 조회 로직 구현
			// gatherId와 userId로 유저가 모임에 포함되어있는지 확인 => gathering_mate 엔티티 필요
			gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
					.userId(userId)
					.gatherId(query.getGatherId())
					.build())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			// 확인됐으면 gatherId로 전시회 exhId에 대해 저장된 날짜 가져오기 => gatheringExh 엔티티 필요
			List<GatheringExhEntity> entities = gatheringExhRepository.findByGatherIdAndExhId(query.getGatherId(),
				query.getExhId());
			for (GatheringExhEntity entity : entities) {
				if (entity.getVisitDate() == null) { // 날짜 모름일 때는 건너뜀.
					continue;
				}
				dates.add(entity.getVisitDate());
			}
		}
		return FindStoredDateResult.findByStoredDate(query.getExhId(), null, dates);
	}

	@Override
	public List<FindExhResult> getExhList(ExhListFindQuery query) throws IOException {
		List<FindExhResult> results = new ArrayList<>();
		List<ExhEntity> exhEntityList = exhRepository.findAll();

		if (query.getSearchName() != null) {
			// searchName으로 exh 테이블에 검색 조회 (exhName, gallery)
			for (ExhEntity exh : exhEntityList) {
				if (exh.getExhName().contains(query.getSearchName())
					|| exh.getGallery().contains(query.getSearchName())) {
					results.add(getFindExhResult(exh));
				}
			}
		} else if (query.getDate() != null) {
			// date 날짜에 진행 중인 전시회 검색 (periodStart <= date and date <= periodEnd)
			for (ExhEntity exh : exhEntityList) {
				if (isProceedExh(exh, query.getDate())) {
					results.add(getFindExhResult(exh));
				}
			}
		} else if (query.getExhCategory() != null) {
			// field or price or state로 전시회 검색 (field= and price = and state)
			for (ExhEntity exh : exhEntityList) {
				boolean field = checkField(query.getExhCategory().getField(), exh);
				boolean price = checkPrice(query.getExhCategory().getPrice(), exh);
				boolean state = checkState(query.getExhCategory().getState(), exh);
				if (field && price && state) {
					results.add(getFindExhResult(exh));
				}
			}
		} else {
			// all
			for (ExhEntity exh : exhEntityList) {
				results.add(getFindExhResult(exh));
			}
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

	@Override
	public List<FindDiaryResult> getAllOfExhIdDiaries(Long exhId) throws IOException {
		List<FindDiaryResult> results = new ArrayList<>();
		//해당 exhId의 user_Exh에서 확인 후, solo_Diary에서 가져오기
		ExhEntity exh = exhRepository.findByExhId(exhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		List<UserExhEntity> userEntities = userExhRepository.findByExhId(exhId);
		for (UserExhEntity userEntity : userEntities) {
			List<MydiaryEntity> diaries = mydiaryRepository.findByUserExhId(userEntity.getUserExhId());
			UserEntity user;

			// 유저가 탈퇴하여 userId가 null인 경우 고려
			if (userEntity.getUserId() == null) {
				user = UserEntity.builder().nickname("전시 메이트").build();
			} else {
				user = userRepository.findByUserId(userEntity.getUserId())
					.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			}
			for (MydiaryEntity diary : diaries) {
				if (!diary.getDiaryPrivate()) {
					continue;
				}
				results.add(FindDiaryResult.findSoloDiary(diary, userEntity, user, exh,
					imageTransfer.downloadImage(exh.getPoster())));
			}
		}

		//해당 exhId의 gather_Exh에서 확인 후, gather_Diary에서 가져오기
		List<GatheringExhEntity> gatherEntities = gatheringExhRepository.findByExhId(exhId);
		for (GatheringExhEntity gatherEntity : gatherEntities) {
			List<GatheringDiaryEntity> gDiaries = gatheringDiaryRepository.findByGatheringExhId(
				gatherEntity.getGatheringExhId());
			GatheringEntity gatherName = gatheringRepository.findByGatherId(gatherEntity.getGatherId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

			for (GatheringDiaryEntity gDiary : gDiaries) {
				if (!gDiary.getDiaryPrivate()) {
					continue;
				}
				UserEntity user;

				// 유저가 탈퇴하여 userId가 -null인 경우 고려
				if (gDiary.getUserId() == null) {
					user = UserEntity.builder().nickname("전시 메이트").build();
				} else {
					user = userRepository.findByUserId(gDiary.getUserId())
						.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
				}
				results.add(FindDiaryResult.findGatheringDiary(gDiary, gatherEntity,
					gatherName, user, exh, imageTransfer.downloadImage(exh.getPoster())));
			}
		}
		return results;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
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
