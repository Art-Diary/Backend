package klieme.artdiary.mateexhs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;

@Service
public class MateExhsService implements MateExhsReadUseCase {
	private final UserExhRepository userExhRepository;
	private final MydiaryRepository mydiaryRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final ExhRepository exhRepository;
	private final MateRepository mateRepository;

	@Autowired
	public MateExhsService(UserExhRepository userExhRepository, MydiaryRepository mydiaryRepository,
		GatheringDiaryRepository gatheringDiaryRepository, GatheringExhRepository gatheringExhRepository,
		ExhRepository exhRepository, MateRepository mateRepository) {
		this.userExhRepository = userExhRepository;
		this.mydiaryRepository = mydiaryRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.exhRepository = exhRepository;
		this.mateRepository = mateRepository;
	}

	@Override
	public List<FindMateExhsResult> getMateExhsList(MateExhsFindQuery query) {
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
		// 반환
		List<FindMateExhsResult> result = new ArrayList<>();
		for (Long exhId : checkExhs) {
			Optional<ExhEntity> exh = exhRepository.findByExhId(exhId);
			/* TODO
			 * 친구가 작성한 글들의 평점?으로 구현함.
			 * 저장된 포스터 사진 있으면 구현
			 * 아래 코드의 null 수정 필요
			 */
			Double averageRate = countDiary.get(exhId) == 0 ? 0.0 : sumDiaryRate.get(exhId) / countDiary.get(exhId);
			exh.ifPresent(exhEntity -> result.add(FindMateExhsResult.findMateExhs(exhEntity, null, averageRate)));
		}
		return result;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
