package klieme.artdiary.solo.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.image.ImageType;
import klieme.artdiary.common.image.S3ImageTransfer;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;

@Service
public class MyDiaryService implements MyDiaryOperationUseCase, MyDiaryReadUseCase {
	private final ExhRepository exhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final S3ImageTransfer s3ImageTransfer;

	@Autowired
	public MyDiaryService(ExhRepository exhRepository, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository, S3ImageTransfer s3ImageTransfer) {
		this.exhRepository = exhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.s3ImageTransfer = s3ImageTransfer;
	}

	@Transactional
	@Override
	public List<FindMyDiaryResult> createMyDiary(MyDiaryCreateUpdateCommand command) {
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
			.initDate(LocalDateTime.now())
			.writeDate(command.getWriteDate())
			.saying(command.getSaying() == null ? "" : command.getSaying())
			.writerId(userEntity.getUserId())
			.exhVisitId(command.getExhVisitId())
			.build();
		diaryRepository.save(newDiary);
		saveThumbnail(command.getThumbnail(), newDiary);

		String changedContents = s3ImageTransfer.uploadContentImagesToStorage(
			S3ImageTransfer.UploadContentImagesQuery.builder()
				.images(command.getFiles())
				.diaryId(newDiary.getDiaryId())
				.contents(command.getContents())
				.build());

		newDiary.updateDiary(DiaryEntity.builder().contents(changedContents).build());
		diaryRepository.save(newDiary);
		return getMyDiaryList(userEntity, exhEntity, null);
	}

	@Override
	public List<FindMyDiaryResult> getMyDiaries(MyDiariesFindQuery query) {
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
	public List<FindMyDiaryResult> updateMyDiary(MyDiaryCreateUpdateCommand command) {
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

		String changedContents = s3ImageTransfer.uploadContentImagesToStorage(
			S3ImageTransfer.UploadContentImagesQuery.builder()
				.images(command.getFiles())
				.diaryId(diaryEntity.getDiaryId())
				.contents(command.getContents())
				.build());
		diaryEntity.updateDiary(DiaryEntity.builder()
			.title(command.getTitle())
			.rate(command.getRate())
			.diaryPrivate(command.getDiaryPrivate())
			.contents(changedContents)
			.writeDate(command.getWriteDate())
			.saying(command.getSaying() == null ? "" : command.getSaying())
			.exhVisitId(command.getExhVisitId())
			.build());

		if (command.getThumbnail() == null || !Objects.equals(command.getThumbnail().getOriginalFilename(),
			diaryEntity.getThumbnail())) {
			saveThumbnail(command.getThumbnail(), diaryEntity);
		}
		diaryRepository.save(diaryEntity);
		return getMyDiaryList(userEntity, exhEntity, null);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private UserEntity getUser() {
		return getCurrentUserEntity();
	}

	private List<FindMyDiaryResult> getMyDiaryList(UserEntity userEntity, ExhEntity exhEntity,
		MyDiariesFindQuery query) {
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
					query.getGatherId(), false, query.getVisitDate(), false);
			} else if (query.getGatherId() == null && query.getVisitDate() != null) {
				// 캘린더 조회: solo && visitdate
				diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), true, null,
					false, query.getVisitDate(), false);
			} else if (query.getGatherId() == null && query.getForget() != null && query.getForget()) {
				// 캘린더 조회: solo && forget=true
				diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), true, null, true,
					null, false);
			}
		} else {
			// 1. 내 기록 조회: forget, visitDate, gatherId 없는 경우
			diaryList = diaryRepository.getDiaryList(userEntity.getUserId(), exhEntity.getExhId(), null, null, false,
				null, false);
		}
		assert diaryList != null;
		for (Map<String, Object> item : diaryList) {
			DiaryEntity diary = (DiaryEntity)item.get("diaryEntity");
			ExhVisitEntity exhVisit = (ExhVisitEntity)item.get("exhVisitEntity");
			GatheringEntity gathering = (GatheringEntity)item.get("gatheringEntity");

			if (diary != null && exhVisit != null) {
				results.add(
					FindMyDiaryResult.findByMyDiary(userEntity, exhEntity, exhVisit, diary, gathering));
			}
		}
		return results;
	}

	private void saveThumbnail(MultipartFile inputThumbnail, DiaryEntity saveEntity) {
		// 사진 업로드
		String uploadImageUrl = s3ImageTransfer.uploadImageToStorage(
			S3ImageTransfer.UploadQuery.builder()
				.type(ImageType.THUMBNAIL)
				.image(inputThumbnail)
				.diaryId(saveEntity.getDiaryId())
				.prevImagePath(saveEntity.getThumbnail())
				.build());

		saveEntity.updateThumbnail(uploadImageUrl);
	}
}
