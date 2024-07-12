package klieme.artdiary.mate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.Tuple;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.solo.data_access.entity.MydiaryEntity;
import klieme.artdiary.solo.data_access.entity.UserExhEntity;
import klieme.artdiary.solo.data_access.repository.MydiaryRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class MateExhsService implements MateExhsReadUseCase {

	private final UserRepository userRepository;
	private final MydiaryRepository mydiaryRepository;
	private final GatheringRepository gatheringRepository;
	private final ExhRepository exhRepository;
	private final MateRepository mateRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public MateExhsService(UserRepository userRepository, MydiaryRepository mydiaryRepository,
		GatheringRepository gatheringRepository, ExhRepository exhRepository, MateRepository mateRepository,
		ImageTransfer imageTransfer) {
		this.userRepository = userRepository;
		this.mydiaryRepository = mydiaryRepository;
		this.gatheringRepository = gatheringRepository;
		this.exhRepository = exhRepository;
		this.mateRepository = mateRepository;
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

		List<MateExhsReadUseCase.FindMateDiaryResult> diaries = new ArrayList<>();

		//친구의 개인 기록 - user_exh에서 확인
		List<Tuple> userExhDiaryList = mydiaryRepository.getMyDiaryListInSoloWithJoin(mateEntity.getUserId(),
			mateExhEntity.getExhId(), true);

		for (Tuple tuple : userExhDiaryList) {
			UserExhEntity userExh = tuple.get(0, UserExhEntity.class);
			MydiaryEntity diary = tuple.get(1, MydiaryEntity.class);

			if (diary != null && userExh != null) {
				String thumbnail = imageTransfer.downloadImage(diary.getThumbnail());
				diaries.add(MateExhsReadUseCase.FindMateDiaryResult.findMateSoloDiary(diary, mateEntity, userExh,
					mateExhEntity, thumbnail));
			}
		}

		//친구의 모임 기록 - gather_exh에서 확인
		List<Tuple> gatherExhDiaryList = gatheringRepository.getMyDiaryListInGatheringWithJoin(mateEntity.getUserId(),
			mateExhEntity.getExhId(), true);

		for (Tuple tuple : gatherExhDiaryList) {
			GatheringEntity gathering = tuple.get(0, GatheringEntity.class);
			GatheringExhEntity gatheringExh = tuple.get(1, GatheringExhEntity.class);
			GatheringDiaryEntity gatheringDiary = tuple.get(2, GatheringDiaryEntity.class);

			if (gathering != null && gatheringExh != null && gatheringDiary != null) {
				String thumbnail = imageTransfer.downloadImage(gatheringDiary.getThumbnail());
				diaries.add(MateExhsReadUseCase.FindMateDiaryResult.findMateGatheringDiary(gatheringDiary, mateEntity,
					gathering, gatheringExh, mateExhEntity, thumbnail));
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
