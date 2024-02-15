package klieme.artdiary.mydiarys.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
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
public class MydiaryService implements MydiaryOperationUseCase, MydiaryReadUseCase {

	private final MydiaryRepository mydiaryRepository;
	private final UserExhRepository userExhRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final GatheringRepository gatheringRepository;
	private final UserRepository userRepository;
	private final ExhRepository exhRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final GatheringMateRepository gatheringMateRepository;

	@Autowired
	public MydiaryService(MydiaryRepository mydiaryRepository, UserExhRepository userExhRepository,
		GatheringExhRepository gatheringExhRepository, GatheringRepository gatheringRepository,
		UserRepository userRepository, ExhRepository exhRepository, GatheringDiaryRepository gatheringDiaryRepository,
		GatheringMateRepository gatheringMateRepository) {
		this.mydiaryRepository = mydiaryRepository;
		this.userExhRepository = userExhRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.gatheringRepository = gatheringRepository;
		this.userRepository = userRepository;
		this.exhRepository = exhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.gatheringMateRepository = gatheringMateRepository;
	}

	@Override
	public List<FindMyDiaryResult> createMyDiary(MydiaryCreateCommand command) {
		// user 데이터
		UserEntity userEntity = getUser();
		// exh 데이터
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 개인 또는 모임의 일정에 유저와 exhId가 포함되어있는지 확인 => userExhId, gatherExhId로 확인
		if (command.getUserExhId() != -1) { // 개인
			// userExhId, exhId, userId 검증
			UserExhEntity storedUserExhEntity = userExhRepository.findByUserExhId(command.getUserExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (!storedUserExhEntity.getExhId().equals(command.getExhId())
				|| !storedUserExhEntity.getUserId().equals(userEntity.getUserId())) {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
			// 디비에 데이터 저장
			MydiaryEntity saveEntity = MydiaryEntity.builder()
				.title(command.getTitle())
				.rate(command.getRate())
				.diaryPrivate(command.getDiaryPrivate())
				.contents(command.getContents())
				.thumbnail(command.getThumbnail())
				.writeDate(command.getWriteDate())
				.saying(command.getSaying())
				.userExhId(command.getUserExhId())
				.build();
			mydiaryRepository.save(saveEntity);
		} else { // 모임
			// gatherExhId, exhId, userId 검증
			GatheringExhEntity storedGatherExhEntity = gatheringExhRepository.findByGatheringExhId(
				command.getGatheringExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (!storedGatherExhEntity.getExhId().equals(command.getExhId())) {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
			// 유저가 모임에 속해 있는지 확인
			gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
				.gatherId(storedGatherExhEntity.getGatherId())
				.userId(userEntity.getUserId())
				.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			// 디비에 데이터 저장
			GatheringDiaryEntity saveEntity = GatheringDiaryEntity.builder()
				.title(command.getTitle())
				.rate(command.getRate())
				.diaryPrivate(command.getDiaryPrivate())
				.contents(command.getContents())
				.thumbnail(command.getThumbnail())
				.writeDate(command.getWriteDate())
				.saying(command.getSaying())
				.userId(userEntity.getUserId())
				.gatheringExhId(command.getGatheringExhId())
				.build();
			gatheringDiaryRepository.save(saveEntity);
		}
		return getMyDiaryList(userEntity, exhEntity);
	}

	@Override
	public List<FindMyDiaryResult> getMyDiaries(MyDiariesFindQuery query) {
		// user 데이터
		UserEntity userEntity = getUser();
		// exh 데이터
		ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		return getMyDiaryList(userEntity, exhEntity);
	}

	@Override
	public void deleteMyDiary(Long exhId, Boolean solo, Long diaryId) {
		//solo= true인 경우
		if (solo) {
			//solo_diary에서 solodiaryId로 userexhId를 확인
			MydiaryEntity soloDiaryEntity = mydiaryRepository.findBySoloDiaryId(diaryId)
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

			//userexhId로 user_exh에서 exhid가 가지고 있는 exhId와 맞는지 확인, userId가 userID와 맞는지 확인,마지막으로 solo_diary에서 solodiaryId 삭제
			UserExhEntity userExhEntity = userExhRepository.findByUserExhId(soloDiaryEntity.getUserExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (userExhEntity.getExhId().equals(exhId) && userExhEntity.getUserId().equals(getUserId())) {
				mydiaryRepository.delete(soloDiaryEntity);
			} else {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
		} else {//solo=false인 경우
			//gatering_diary에서 gatherdiaryId와 userId가 가지고 있는 userId가 맞는지 확인
			GatheringDiaryEntity gatheringDiaryEntity = gatheringDiaryRepository.findByGatherDiaryIdAndUserId(diaryId,
				getUserId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			//gatherexhId로 gather_exh에서 exhId가 가지고 있는 exhId가 맞는지 확인, gathering_diary에서 gatehrdiarId 삭제
			GatheringExhEntity gatheringExhEntity = gatheringExhRepository.findByGatheringExhId(
					gatheringDiaryEntity.getGatheringExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (gatheringExhEntity.getExhId().equals(exhId)) {
				gatheringDiaryRepository.delete(gatheringDiaryEntity);
			} else {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
		}
	}

	@Override
	public List<FindMyDiaryResult> updateMyDiary(MyDiaryUpdateCommand command) {
		// user 데이터
		UserEntity userEntity = getUser();
		// exh 데이터
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		if (command.getUserExhId() != -1) { // 개인
			// userExhId, exhId, userId 검증
			UserExhEntity storedUserExhEntity = userExhRepository.findByUserExhId(command.getUserExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (!storedUserExhEntity.getExhId().equals(command.getExhId())
				|| !storedUserExhEntity.getUserId().equals(userEntity.getUserId())) {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
			// 저장된 데이터 조회
			MydiaryEntity saved = mydiaryRepository.findBySoloDiaryId(command.getDiaryId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			// 데이터 수정
			saved.updateDiary(
				Objects.equals(saved.getTitle(), command.getTitle()) ? saved.getTitle() : command.getTitle(),
				Objects.equals(saved.getRate(), command.getRate()) ? saved.getRate() : command.getRate(),
				Objects.equals(saved.getDiaryPrivate(), command.getDiaryPrivate())
					? saved.getDiaryPrivate() : command.getDiaryPrivate(),
				Objects.equals(saved.getContents(), command.getContents())
					? saved.getContents() : command.getContents(),
				Objects.equals(saved.getThumbnail(), command.getThumbnail())
					? saved.getThumbnail() : command.getThumbnail(),
				Objects.equals(saved.getWriteDate(), command.getWriteDate())
					? saved.getWriteDate() : command.getWriteDate(),
				Objects.equals(saved.getSaying(), command.getSaying()) ? saved.getSaying() : command.getSaying());
			mydiaryRepository.save(saved);
		} else { // 모임
			// 저장된 데이터 조회
			GatheringDiaryEntity saved = gatheringDiaryRepository.findByGatherDiaryIdAndUserId(command.getDiaryId(),
				userEntity.getUserId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			// gatherExhId, userId 검증
			if (!saved.getGatheringExhId().equals(command.getGatheringExhId())) {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
			// exhId 검증
			GatheringExhEntity storedGatherExhEntity = gatheringExhRepository.findByGatheringExhId(
				command.getGatheringExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (!storedGatherExhEntity.getExhId().equals(command.getExhId())) {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
			// 디비에 데이터 수정
			saved.updateDiary(
				Objects.equals(saved.getTitle(), command.getTitle()) ? saved.getTitle() : command.getTitle(),
				Objects.equals(saved.getRate(), command.getRate()) ? saved.getRate() : command.getRate(),
				Objects.equals(saved.getDiaryPrivate(), command.getDiaryPrivate())
					? saved.getDiaryPrivate() : command.getDiaryPrivate(),
				Objects.equals(saved.getContents(), command.getContents())
					? saved.getContents() : command.getContents(),
				Objects.equals(saved.getThumbnail(), command.getThumbnail())
					? saved.getThumbnail() : command.getThumbnail(),
				Objects.equals(saved.getWriteDate(), command.getWriteDate())
					? saved.getWriteDate() : command.getWriteDate(),
				Objects.equals(saved.getSaying(), command.getSaying()) ? saved.getSaying() : command.getSaying());
			gatheringDiaryRepository.save(saved);
		}
		return getMyDiaryList(userEntity, exhEntity);
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}

	private UserEntity getUser() {
		return userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}

	private List<FindMyDiaryResult> getMyDiaryList(UserEntity userEntity, ExhEntity exhEntity) {
		List<FindMyDiaryResult> results = new ArrayList<>();
		// (solo_diary) exhId 전시회에 대한 개인의 기록 리스트 조회
		List<UserExhEntity> storedUserExhList = userExhRepository.findByUserIdAndExhId(userEntity.getUserId(),
			exhEntity.getExhId());
		for (UserExhEntity storedUserExh : storedUserExhList) {
			List<MydiaryEntity> savedEntityList = mydiaryRepository.findByUserExhId(storedUserExh.getUserExhId());
			// 함수의 반환형에 맞도록 변환
			for (MydiaryEntity mydiaryEntity : savedEntityList) {
				results.add(FindMyDiaryResult.findByMyDiary(mydiaryEntity, userEntity, storedUserExh, exhEntity));
			}
		}
		// (gathering_diary) exhId 전시회에 대한 모임의 내 기록 리스트 반환
		// gathering_diary에서 userId에 해당하는 데이터 목록 조회
		List<GatheringDiaryEntity> gatheringDiaryEntityList = gatheringDiaryRepository.findByUserId(
			userEntity.getUserId());
		for (GatheringDiaryEntity gatheringDiaryEntity : gatheringDiaryEntityList) {
			// gathering_exh에서 exhId 걸러내고
			Optional<GatheringExhEntity> gatheringExhEntity = gatheringExhRepository.findByGatheringExhId(
				gatheringDiaryEntity.getGatheringExhId());
			if (gatheringExhEntity.isEmpty() || !gatheringExhEntity.get().getExhId().equals(exhEntity.getExhId())) {
				continue;
			}
			// gatherId로 gathering 데이터 가져오기.
			GatheringEntity gatheringEntity = gatheringRepository.findByGatherId(gatheringExhEntity.get().getGatherId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			// 함수의 반환형에 맞도록 변환
			results.add(FindMyDiaryResult.findByGatheringDiary(gatheringDiaryEntity, userEntity, gatheringEntity,
				gatheringExhEntity.get(), exhEntity));
		}
		// 방문날짜순
		results.sort(Comparator.comparing(FindMyDiaryResult::getVisitDate));
		return results;
	}
}
