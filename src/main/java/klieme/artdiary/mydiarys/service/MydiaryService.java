package klieme.artdiary.mydiarys.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
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
		ExhEntity exhEntity;
		// 개인 또는 모임의 일정에 유저와 exhId가 포함되어있는지 확인 => userExhId, gatherExhId로 확인
		if (command.getUserExhId() != -1) { // 개인
			// userExhId, exhId, userId 검증
			UserExhEntity storedUserExhEntity = userExhRepository.findByUserExhId(command.getUserExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			exhEntity = exhRepository.findByExhId(command.getExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (!Objects.equals(storedUserExhEntity.getExhId(), command.getExhId())) {
				throw new ArtDiaryException(MessageType.NOT_FOUND);
			}
			if (!Objects.equals(storedUserExhEntity.getUserId(), userEntity.getUserId())) {
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
			exhEntity = exhRepository.findByExhId(command.getExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			if (!Objects.equals(storedGatherExhEntity.getExhId(), command.getExhId())) {
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

	private Long getUserId() {
		return 3L;
	}

	private UserEntity getUser() {
		return userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}

	private List<FindMyDiaryResult> getMyDiaryList(UserEntity userEntity, ExhEntity exhEntity) {
		List<FindMyDiaryResult> results = new ArrayList<>();
		// exhId 전시회에 대한 개인의 기록 리스트 조회
		List<UserExhEntity> storedUserExhList = userExhRepository.findByExhId(exhEntity.getExhId());
		for (UserExhEntity storedUserExh : storedUserExhList) {
			List<MydiaryEntity> savedEntityList = mydiaryRepository.findByUserExhId(storedUserExh.getUserExhId());
			// 함수의 반환형에 맞도록 변환
			for (MydiaryEntity mydiaryEntity : savedEntityList) {
				results.add(FindMyDiaryResult.findByMyDiary(mydiaryEntity, userEntity, storedUserExh, exhEntity));
			}
		}
		// exhId 전시회에 대한 모임의 내 기록 리스트 반환
		// 내가 속한 모임 리스트
		List<GatheringMateEntity> storedGatheringMateList = gatheringMateRepository.findByGatheringMateIdUserId(
			userEntity.getUserId());
		for (GatheringMateEntity entity : storedGatheringMateList) {
			// 모임의 전시회 일정
			List<GatheringExhEntity> storedGatherExhList = gatheringExhRepository.findByGatherIdAndExhId(
				entity.getGatheringMateId().getGatherId(), exhEntity.getExhId());
			if (storedGatherExhList.isEmpty()) {
				continue;
			}
			// 모임 조회
			GatheringEntity storedGatheringList = gatheringRepository.findByGatherId(
					entity.getGatheringMateId().getGatherId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			for (GatheringExhEntity storedGatherExh : storedGatherExhList) {
				// 일정의 기록 리스트
				List<GatheringDiaryEntity> savedEntityList = gatheringDiaryRepository.findByGatheringExhId(
					storedGatherExh.getGatheringExhId());
				// 함수의 반환형에 맞도록 변환
				for (GatheringDiaryEntity gatheringDiaryEntity : savedEntityList) {
					results.add(
						FindMyDiaryResult.findByGatheringDiary(gatheringDiaryEntity, userEntity, storedGatheringList,
							storedGatherExh, exhEntity));
				}
			}
		}
		// 작성순
		results.sort(Comparator.comparing(FindMyDiaryResult::getWriteDate));
		return results;
	}
}
