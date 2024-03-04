package klieme.artdiary.mateexhs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class MateExhsService implements MateExhsReadUseCase {

	private final UserRepository userRepository;
	private final UserExhRepository userExhRepository;
	private final MydiaryRepository mydiaryRepository;
	private final GatheringRepository gatheringRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final ExhRepository exhRepository;
	private final MateRepository mateRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public MateExhsService(UserRepository userRepository, UserExhRepository userExhRepository,
		MydiaryRepository mydiaryRepository,
		GatheringRepository gatheringRepository,
		GatheringDiaryRepository gatheringDiaryRepository, GatheringExhRepository gatheringExhRepository,
		ExhRepository exhRepository, MateRepository mateRepository, GatheringMateRepository gatheringMateRepository,
		ImageTransfer imageTransfer) {
		this.userRepository = userRepository;
		this.userExhRepository = userExhRepository;
		this.mydiaryRepository = mydiaryRepository;
		this.gatheringRepository = gatheringRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.exhRepository = exhRepository;
		this.mateRepository = mateRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.imageTransfer = imageTransfer;
	}

	@Override
	public List<FindMateExhsResult> getMateExhsList(MateExhsFindQuery query) throws IOException {
		// 내 친구가 맞는지 확인 - exh_mate 확인
		mateRepository.findByFromUserIdAndToUserId(getUserId(), query.getMateId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 친구의 기록이 있는 전시회 조회
		List<Long> checkExhs = new ArrayList<>(); // 같은 전시회지만 방문 날짜가 다른 경우의 중복을 없애기 위해 사용
		HashMap<Long, Integer> countDiary = new HashMap<>(); // 전시회에 대한 기록 개수
		HashMap<Long, Double> sumDiaryRate = new HashMap<>(); // 전시회에 대한 기록 별점 합
		// 1. userExh에서 친구가 작성한 기록이 있는 경우
		List<UserExhEntity> mateExhEntities = userExhRepository.findByUserId(query.getMateId());
		for (UserExhEntity mateExhEntity : mateExhEntities) {
			List<MydiaryEntity> mateDiaryEntities = mydiaryRepository.findByUserExhId(mateExhEntity.getUserExhId());

			countDiary.putIfAbsent(mateExhEntity.getExhId(), 0);
			sumDiaryRate.putIfAbsent(mateExhEntity.getExhId(), 0.0);
			for (MydiaryEntity mateDiaryEntity : mateDiaryEntities) {
				Integer countExh = countDiary.get(mateExhEntity.getExhId());
				Double sumExhRate = sumDiaryRate.get(mateExhEntity.getExhId());
				countDiary.put(mateExhEntity.getExhId(), countExh + 1);
				sumDiaryRate.put(mateExhEntity.getExhId(), sumExhRate + mateDiaryEntity.getRate());
			}
			// 전시회 중복 제거
			if (!checkExhs.contains(mateExhEntity.getExhId())) {
				checkExhs.add(mateExhEntity.getExhId());
			}
		}
		// 2. gatheringDiary애서 userId를 통해 친구가 작성한 기록이 있는 경우
		List<GatheringDiaryEntity> gatheringDiaryEntities = gatheringDiaryRepository.findByUserId(query.getMateId());
		for (GatheringDiaryEntity gatheringDiary : gatheringDiaryEntities) {
			Optional<GatheringExhEntity> gatheringExhEntity = gatheringExhRepository.findByGatheringExhId(
				gatheringDiary.getGatheringExhId());
			if (gatheringExhEntity.isEmpty()) {
				continue;
			}
			int countExh = countDiary.get(gatheringExhEntity.get().getExhId()) == null ? 0 :
				countDiary.get(gatheringExhEntity.get().getExhId());
			Double sumExhRate = sumDiaryRate.get(gatheringExhEntity.get().getExhId()) == null ? 0.0 :
				sumDiaryRate.get(gatheringExhEntity.get().getExhId());
			countDiary.put(gatheringExhEntity.get().getExhId(), countExh + 1);
			sumDiaryRate.put(gatheringExhEntity.get().getExhId(), sumExhRate + gatheringDiary.getRate());
			// 전시회 중복 제거
			if (!checkExhs.contains(gatheringExhEntity.get().getExhId())) {
				checkExhs.add(gatheringExhEntity.get().getExhId());
			}
		}
		// 반환 (친구가 작성한 글들의 평점?으로 구현함.)
		List<FindMateExhsResult> result = new ArrayList<>();
		for (Long exhId : checkExhs) {
			Optional<ExhEntity> exh = exhRepository.findByExhId(exhId);
			Double averageRate = countDiary.get(exhId) == 0 ? 0.0 : sumDiaryRate.get(exhId) / countDiary.get(exhId);

			if (exh.isPresent()) {
				String poster = imageTransfer.downloadImage(exh.get().getPoster());
				result.add(FindMateExhsResult.findMateExhs(exh.get(), poster, averageRate));
			}
		}
		return result;
	}

	@Override
	public List<FindMateDiaryResult> getMateDiaryList(MateDiaryFindQuery query) throws IOException {

		// 내 친구가 맞는지 확인 - exh_mate 확인
		mateRepository.findByFromUserIdAndToUserId(getUserId(), query.getMateId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		UserEntity mateEntity = userRepository.findByUserId(query.getMateId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		ExhEntity mateExhEntity = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		//친구의 개인 기록 - user_exh에서 확인
		List<UserExhEntity> uEntities = userExhRepository.findByUserIdAndExhId(mateEntity.getUserId(),
			mateExhEntity.getExhId());
		List<MateExhsReadUseCase.FindMateDiaryResult> diaries = new ArrayList<>();
		for (UserExhEntity uEntity : uEntities) {
			List<MydiaryEntity> diaryList = mydiaryRepository.findByUserExhId(uEntity.getUserExhId());
			for (MydiaryEntity diary : diaryList) {
				if (!diary.getDiaryPrivate()) {
					continue;
				}
				String thumbnail = imageTransfer.downloadImage(diary.getThumbnail());
				diaries.add(MateExhsReadUseCase.FindMateDiaryResult.findMateSoloDiary(diary, mateEntity, uEntity,
					mateExhEntity, thumbnail));
			}
		}

		//친구의 모임 기록 - gather_exh에서 확인
		//모임있는지 확인
		List<GatheringMateEntity> gEntities = gatheringMateRepository.findByGatheringMateIdUserId(query.getMateId());

		//모임있을시, userId도 확인하고(내기록만 가져와야하니까), 한 모임의 한 전시를
		for (GatheringMateEntity gEntity : gEntities) {
			GatheringEntity gatheringEntity = gatheringRepository.findByGatherId(gEntity.getGatheringMateId()
				.getGatherId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			List<GatheringExhEntity> gatheringExhEntities = gatheringExhRepository.findByGatherIdAndExhId(
				gEntity.getGatheringMateId().getGatherId(), mateExhEntity.getExhId());
			for (GatheringExhEntity gatheringExhEntity : gatheringExhEntities) {
				List<GatheringDiaryEntity> gatheringDiaryList = gatheringDiaryRepository.findByGatheringExhId(
					gatheringExhEntity.getGatheringExhId());
				for (GatheringDiaryEntity gatheringDiary : gatheringDiaryList) {
					String thumbnail = imageTransfer.downloadImage(gatheringDiary.getThumbnail());
					diaries.add(
						MateExhsReadUseCase.FindMateDiaryResult.findMateGatheringDiary(gatheringDiary, mateEntity,
							gatheringEntity, gatheringExhEntity, mateExhEntity, thumbnail));
				}
			}

		}
		return diaries;

	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
