package klieme.artdiary.mate.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.image.ImageTransfer;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class MateExhService implements MateExhReadUseCase {

	private final UserRepository userRepository;
	private final DiaryRepository diaryRepository;
	private final ExhRepository exhRepository;
	private final MateRepository mateRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public MateExhService(UserRepository userRepository, DiaryRepository diaryRepository, ExhRepository exhRepository,
		MateRepository mateRepository, ImageTransfer imageTransfer) {
		this.userRepository = userRepository;
		this.diaryRepository = diaryRepository;
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

		List<Map<String, Object>> mateDiarySumRateAndCountList = diaryRepository.getMyDiarySumRateAndCount(mateId,
			true);
		List<FindMateExhsResult> result = new ArrayList<>();

		for (Map<String, Object> mateDiarySumRateAndCount : mateDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)mateDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)mateDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)mateDiarySumRateAndCount.get("exhibition");
			// averageRate & poster
			double averageRate = sumOfRate / countOfDiary;
			String poster = imageTransfer.downloadImage(exh.getPoster());

			result.add(FindMateExhsResult.findMateExhs(exh, averageRate, poster));
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

		List<MateExhReadUseCase.FindMateDiaryResult> results = new ArrayList<>();
		// 친구의 개인 기록 - exh_visit에서 확인

		List<Map<String, Object>> diaryList = diaryRepository.getDiaryList(mateEntity.getUserId(),
			mateExhEntity.getExhId(), null, null, false,
			null, true);

		for (Map<String, Object> item : diaryList) {
			DiaryEntity diary = (DiaryEntity)item.get("diaryEntity");
			ExhVisitEntity exhVisit = (ExhVisitEntity)item.get("exhVisitEntity");
			GatheringEntity gathering = (GatheringEntity)item.get("gatheringEntity");

			if (diary != null && exhVisit != null) {
				String thumbnail = imageTransfer.downloadImage(diary.getThumbnail());
				results.add(
					MateExhReadUseCase.FindMateDiaryResult.findMateDiary(diary, exhVisit, mateEntity, mateExhEntity,
						gathering, thumbnail));
			}
		}
		results.sort(Comparator.comparing(FindMateDiaryResult::getInitDate));
		return results;
	}

	private Long getUserId() {
		return getCurrentUserId();
	}
}
