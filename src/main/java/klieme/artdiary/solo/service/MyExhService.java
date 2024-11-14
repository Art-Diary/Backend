package klieme.artdiary.solo.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.solo.info.StoredDateInfo;

@Service
public class MyExhService implements MyExhReadUseCase, MyExhOperationUseCase {
	private final ExhRepository exhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;

	@Autowired
	public MyExhService(ExhRepository exhRepository, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository) {
		this.exhRepository = exhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
	}

	@Override
	public List<MyExhReadUseCase.FindMyExhsResult> getMyExhsList() {
		Long userId = getUserId();
		// 내가 작성한 전시회 기록들의 평점 구하기
		List<Map<String, Object>> myDiarySumRateAndCountList = diaryRepository.getMyDiarySumRateAndCount(userId, false);

		List<MyExhReadUseCase.FindMyExhsResult> result = new ArrayList<>();

		for (Map<String, Object> myDiarySumRateAndCount : myDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)myDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)myDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)myDiarySumRateAndCount.get("exhibition");
			// averageRate & poster
			double averageRate = sumOfRate / countOfDiary;

			result.add(MyExhReadUseCase.FindMyExhsResult.findMyExhs(exh, averageRate));
		}
		return result;
	}

	@Override
	public List<FindMyStoredDateResult> getStoredDateOfExhs(MyStoredDateFindQuery query) {
		Long userId = getUserId();
		List<FindMyStoredDateResult> results = new ArrayList<>();

		exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		List<Map<String, Object>> myVisitedDateList = exhVisitRepository.getMyVisitedDateListOfExh(userId,
			query.getExhId());

		if (!myVisitedDateList.isEmpty()) {
			List<StoredDateInfo> dateInfoList = new ArrayList<>();
			ExhVisitEntity firstExhVisit = (ExhVisitEntity)myVisitedDateList.getFirst().get("exhVisit");
			Long checkGatherId = firstExhVisit.getGatherId();

			for (int i = 0; i < myVisitedDateList.size(); i++) {
				ExhVisitEntity exhVisit = (ExhVisitEntity)myVisitedDateList.get(i).get("exhVisit");
				GatheringEntity gathering = (GatheringEntity)myVisitedDateList.get(i).get("gathering");
				ExhVisitEntity nextExhVisit =
					i + 1 < myVisitedDateList.size() ? (ExhVisitEntity)myVisitedDateList.get(i + 1).get("exhVisit") :
						null;

				dateInfoList.add(StoredDateInfo.builder()
					.exhVisitId(exhVisit.getExhVisitId())
					.visitDate(changeDateFormat(exhVisit.getVisitDate()))
					.build());
				if ((nextExhVisit != null && !Objects.equals(checkGatherId, nextExhVisit.getGatherId()))
					|| i == myVisitedDateList.size() - 1) {
					if (checkGatherId == null) {
						// solo
						results.add(FindMyStoredDateResult.findByMyStoredDateSolo(query.getExhId(), dateInfoList));
					} else {
						// gather
						results.add(
							FindMyStoredDateResult.findByMyStoredDateGather(query.getExhId(), gathering,
								dateInfoList));
					}
					checkGatherId = nextExhVisit == null ? null : nextExhVisit.getGatherId();
					dateInfoList = new ArrayList<>();
				}
			}
			if (results.getFirst().getGatherId() != null) {
				results.addFirst(FindMyStoredDateResult.findByMyStoredDateSolo(query.getExhId(), new ArrayList<>()));
			}
		}
		return results;
	}

	@Transactional
	@Override
	public List<FindMyStoredDateResult> addMyExhVisitDate(AddMyExhVisitDateCommand command) {
		// 전시회 아이디 검증
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 전시회 일정에 맞춰 갈 수 있는지 확인
		if (command.getVisitDate() != null) {
			if (exhEntity.getExhPeriodStart().isAfter(command.getVisitDate())
				|| exhEntity.getExhPeriodEnd().isBefore(command.getVisitDate())) {
				throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
			}
		}

		// 관람 날짜 추가
		ExhVisitEntity newExhVisit = ExhVisitEntity.builder()
			.visitDate(command.getVisitDate())
			.userId(getUserId())
			.exhId(exhEntity.getExhId())
			.build();
		// 관람 날짜 중복 확인
		Optional<ExhVisitEntity> checkExhVisit = exhVisitRepository.findByUserIdAndExhIdAndVisitDate(getUserId(),
			exhEntity.getExhId(), command.getVisitDate());

		if (checkExhVisit.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		try {
			exhVisitRepository.save(newExhVisit);
		} catch (DataIntegrityViolationException e) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}

		// 반환
		List<ExhVisitEntity> exhVisitList = exhVisitRepository.findByUserIdAndExhId(getUserId(), exhEntity.getExhId());
		List<FindMyStoredDateResult> results = new ArrayList<>();

		for (ExhVisitEntity exhVisit : exhVisitList) {
			results.add(MyExhReadUseCase.FindMyStoredDateResult.findByMyAllDatesSolo(exhVisit));
		}
		return results;
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}
