package klieme.artdiary.solo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.ImageType;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class MyDiaryService implements MyDiaryOperationUseCase, MyDiaryReadUseCase {
	private final UserRepository userRepository;
	private final ExhRepository exhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public MyDiaryService(UserRepository userRepository, ExhRepository exhRepository,
		ExhVisitRepository exhVisitRepository, DiaryRepository diaryRepository, ImageTransfer imageTransfer) {
		this.userRepository = userRepository;
		this.exhRepository = exhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.imageTransfer = imageTransfer;
	}

	@Transactional
	@Override
	public List<FindMyDiaryResult> createMyDiary(MyDiaryCreateUpdateCommand command) throws IOException {
		// user 데이터
		UserEntity userEntity = getUser();
		// exh 데이터
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// exhVisitId 검증
		Boolean checkExhVisit = exhVisitRepository.checkExhVisitByExhVisitId(command.getExhVisitId(),
			userEntity.getUserId(), exhEntity.getExhId());

		if (!checkExhVisit) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		DiaryEntity newDiary = DiaryEntity.builder()
			.title(command.getTitle())
			.rate(command.getRate())
			.diaryPrivate(command.getDiaryPrivate())
			.contents(command.getContents())
			.initDate(command.getWriteDate())
			.writeDate(command.getWriteDate())
			.saying(command.getSaying())
			.writerId(userEntity.getUserId())
			.exhVisitId(command.getExhVisitId())
			.build();
		diaryRepository.save(newDiary);
		saveThumbnail(command.getThumbnail(), newDiary);
		diaryRepository.save(newDiary);
		return getMyDiaryList(userEntity, exhEntity, null);
	}

	@Override
	public List<FindMyDiaryResult> getMyDiaries(MyDiariesFindQuery query) throws IOException {
		// user 데이터
		UserEntity userEntity = getUser();
		// exh 데이터
		ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		return getMyDiaryList(userEntity, exhEntity,
			query.getGatherId() == null && query.getForget() == null && query.getVisitDate() == null ? null : query);
	}

	@Transactional
	@Override
	public void deleteMyDiary(Long exhId, Long diaryId) {
		DiaryEntity diary = diaryRepository.getDiaryByDiaryIdAndWriterIdAndExhId(diaryId, getUserId(), exhId);

		if (diary == null) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		diaryRepository.delete(diary);
	}

	@Transactional
	@Override
	public List<FindMyDiaryResult> updateMyDiary(MyDiaryCreateUpdateCommand command) throws IOException {
		// user 데이터
		UserEntity userEntity = getUser();
		// exh 데이터
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// exhVisitId 검증
		Boolean checkExhVisit = exhVisitRepository.checkExhVisitByExhVisitId(command.getExhVisitId(),
			userEntity.getUserId(), exhEntity.getExhId());

		if (!checkExhVisit) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}

		DiaryEntity diaryEntity = diaryRepository.findByDiaryId(command.getDiaryId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		diaryEntity.updateDiary(DiaryEntity.builder()
			.title(command.getTitle())
			.rate(command.getRate())
			.diaryPrivate(command.getDiaryPrivate())
			.contents(command.getContents())
			.writeDate(command.getWriteDate())
			.saying(command.getSaying())
			.exhVisitId(command.getExhVisitId())
			.build());
		saveThumbnail(command.getThumbnail(), diaryEntity);
		diaryRepository.save(diaryEntity);
		return getMyDiaryList(userEntity, exhEntity, null);
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}

	private UserEntity getUser() {
		return userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}

	private List<FindMyDiaryResult> getMyDiaryList(UserEntity userEntity, ExhEntity exhEntity,
		MyDiariesFindQuery query) throws IOException {
		List<FindMyDiaryResult> results = new ArrayList<>();
		List<Map<String, Object>> diaryList = null;

		if (query != null) {
			// 2. 캘린더 조회
			// 		- gatherId && visitdate
			// 		- solo && visitdate
			// 		- solo && forget=true
			if (query.getGatherId() != null && query.getVisitDate() != null) {
				// 캘린더 조회: gatherId && visitdate
				diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), false,
					query.getGatherId(), false, query.getVisitDate());
			} else if (query.getGatherId() == null && query.getVisitDate() != null) {
				// 캘린더 조회: solo && visitdate
				diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), true, null,
					false, query.getVisitDate());
			} else if (query.getGatherId() == null && query.getForget() != null && query.getForget()) {
				// 캘린더 조회: solo && forget=true
				diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), true, null, true,
					null);
			}
		} else {
			// 1. 내 기록 조회: forget, visitDate, gatherId 없는 경우
			diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), null, null, false,
				null);
		}
		assert diaryList != null;
		for (Map<String, Object> item : diaryList) {
			DiaryEntity diary = (DiaryEntity)item.get("diaryEntity");
			ExhVisitEntity exhVisit = (ExhVisitEntity)item.get("exhVisitEntity");
			GatheringEntity gathering = (GatheringEntity)item.get("gatheringEntity");

			if (diary != null && exhVisit != null) {
				String thumbnail = imageTransfer.downloadImage(diary.getThumbnail());
				results.add(
					FindMyDiaryResult.findByMyDiary(userEntity, exhEntity, thumbnail, exhVisit, diary, gathering));
			}
		}
		results.sort(Comparator.comparing(FindMyDiaryResult::getInitDate));
		return results;
	}

	private void saveThumbnail(MultipartFile inputThumbnail, DiaryEntity saveEntity) throws IOException {
		// 사진 업로드
		ImageTransfer.FindUploadResult uploadResult = imageTransfer.uploadImageToStorage(
			ImageTransfer.UploadQuery.builder()
				.type(ImageType.THUMBNAIL)
				.image(inputThumbnail)
				.diaryId(saveEntity.getDiaryId())
				.build());

		saveEntity.updateThumbnail(uploadResult.getStoredPath());
	}
}
