package klieme.artdiary.mateexhs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;
import klieme.artdiary.mates.data_access.repository.MateRepository;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;
import klieme.artdiary.myexhs.data_access.entity.UserExhEntity;
import klieme.artdiary.myexhs.data_access.repository.UserExhRepository;
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
		Long mateId = query.getMateId();
		// 내 친구가 맞는지 확인 - exh_mate 확인
		mateRepository.findByFromUserIdAndToUserId(getUserId(), mateId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 1. 전시 메이트가 혼자 방문한 전시회에 대해 작성한 기록이 있는 경우
		List<Map<String, Object>> myStoredExhList = mydiaryRepository.sumRateByUserExhId(mateId, true);
		// 2. 전시 메이트가 모임 안에서 방문한 전시회에 대해 작성한 기록이 있는 경우
		List<Map<String, Object>> myStoredGatherExhList = gatheringRepository.sumRateByGatherExhId(mateId, true);

		//
		HashMap<Long, Long> countDiary = new HashMap<>(); // 전시회에 대한 기록 개수
		HashMap<Long, Double> sumDiaryRate = new HashMap<>(); // 전시회에 대한 기록 별점 합
		HashMap<Long, ExhEntity> exhEntityHashMap = new HashMap<>(); // 전시회에 대한 기록 별점 합

		// 중복 코드를 줄이기 위해 메서드로 분리
		processExhibitionList(myStoredExhList, countDiary, sumDiaryRate, exhEntityHashMap);
		processExhibitionList(myStoredGatherExhList, countDiary, sumDiaryRate, exhEntityHashMap);

		List<Long> exhIds = new ArrayList<>(countDiary.keySet());
		exhIds.sort(Comparator.naturalOrder());

		List<FindMateExhsResult> result = new ArrayList<>();
		for (Long exhId : exhIds) {
			ExhEntity exh = exhEntityHashMap.get(exhId);
			double averageRate = countDiary.get(exhId) == 0 ? 0.0 : sumDiaryRate.get(exhId) / countDiary.get(exhId);
			String poster = imageTransfer.downloadImage(exh.getPoster());
			result.add(FindMateExhsResult.findMateExhs(exh, poster, averageRate));
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
				List<GatheringDiaryEntity> gatheringDiaryList = gatheringDiaryRepository.findByGatherExhId(
					gatheringExhEntity.getGatherExhId());
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

	// 중복 코드를 메서드로 분리
	private void processExhibitionList(List<Map<String, Object>> exhibitionList, HashMap<Long, Long> countDiary,
		HashMap<Long, Double> sumDiaryRate, HashMap<Long, ExhEntity> exhEntityHashMap) {
		for (Map<String, Object> map : exhibitionList) {
			ExhEntity exh = (ExhEntity)map.get("exhibition");
			Long count = (Long)map.get("count");
			Double sumOfRate = (Double)map.get("sumOfRate");

			countDiary.putIfAbsent(exh.getExhId(), 0L);
			sumDiaryRate.putIfAbsent(exh.getExhId(), 0.0);

			Long countExh = countDiary.get(exh.getExhId());
			Double sumExhRate = sumDiaryRate.get(exh.getExhId());

			countDiary.put(exh.getExhId(), countExh + count);
			sumDiaryRate.put(exh.getExhId(), sumExhRate + sumOfRate);
			exhEntityHashMap.put(exh.getExhId(), exh);
		}
	}
}
