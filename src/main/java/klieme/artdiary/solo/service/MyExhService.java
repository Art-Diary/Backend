package klieme.artdiary.solo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gathering.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.solo.data_access.entity.UserExhEntity;
import klieme.artdiary.solo.data_access.repository.MydiaryRepository;
import klieme.artdiary.solo.data_access.repository.UserExhRepository;
import klieme.artdiary.solo.info.StoredDateInfo;

@Service
public class MyExhService implements MyExhReadUseCase, MyExhOperationUseCase {
	private final GatheringMateRepository gatheringMateRepository; // 삭제
	private final GatheringRepository gatheringRepository; // 삭제
	private final GatheringExhRepository gatheringExhRepository; // 삭제
	private final ExhRepository exhRepository;
	private final UserExhRepository userExhRepository; // 삭제
	private final MydiaryRepository mydiaryRepository; // 삭제
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public MyExhService(GatheringMateRepository gatheringMateRepository, GatheringRepository gatheringRepository,
		GatheringExhRepository gatheringExhRepository, UserExhRepository userExhRepository, ExhRepository exhRepository,
		MydiaryRepository mydiaryRepository, ExhVisitRepository exhVisitRepository, DiaryRepository diaryRepository,
		ImageTransfer imageTransfer) {
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringRepository = gatheringRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.userExhRepository = userExhRepository;
		this.exhRepository = exhRepository;
		this.mydiaryRepository = mydiaryRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.imageTransfer = imageTransfer;
	}

	@Override
	public List<MyExhReadUseCase.FindMyExhsResult> getMyExhsList() throws IOException {
		Long userId = getUserId();
		// 내가 작성한 전시회 기록들의 평점 구하기
		List<Map<String, Object>> myDiarySumRateAndCountList = diaryRepository.getMyDiarySumRateAndCount(userId);

		List<MyExhReadUseCase.FindMyExhsResult> result = new ArrayList<>();

		for (Map<String, Object> myDiarySumRateAndCount : myDiarySumRateAndCountList) {
			// map
			Double sumOfRate = (Double)myDiarySumRateAndCount.get("sumOfRate");
			Long countOfDiary = (Long)myDiarySumRateAndCount.get("countOfDiary");
			ExhEntity exh = (ExhEntity)myDiarySumRateAndCount.get("exhibition");
			// averageRate & poster
			double averageRate = sumOfRate / countOfDiary;
			String poster = imageTransfer.downloadImage(exh.getPoster());

			result.add(MyExhReadUseCase.FindMyExhsResult.findMyExhs(exh, averageRate, poster));
		}
		return result;
	}

	@Override
	public List<FindMyStoredDateResult> getStoredDateOfExhs(MyStoredDateFindQuery query) {
		Long userId = getUserId();
		List<FindMyStoredDateResult> results = new ArrayList<>();

		ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		List<Map<String, Object>> myVisitedDateList = exhVisitRepository.getMyVisitedDateListOfExh(userId,
			query.getExhId());

		if (!myVisitedDateList.isEmpty()) {
			List<StoredDateInfo> dateInfoList = new ArrayList<>();
			ExhVisitEntity lastExhVisit = (ExhVisitEntity)myVisitedDateList.get(myVisitedDateList.size() - 1)
				.get("exhVisit");
			Long lastExhVisitId = lastExhVisit.getExhVisitId();
			Long checkGatherId = 0L;

			for (Map<String, Object> myVisitedDateOfExh : myVisitedDateList) {
				ExhVisitEntity exhVisit = (ExhVisitEntity)myVisitedDateOfExh.get("exhVisit");
				GatheringEntity gathering = (GatheringEntity)myVisitedDateOfExh.get("gathering");

				if (checkGatherId == 0L) {
					checkGatherId = exhVisit.getGatherId();
				}
				if (!Objects.equals(checkGatherId, exhVisit.getGatherId())
					|| Objects.equals(exhVisit.getExhVisitId(), lastExhVisitId)) {
					if (checkGatherId == null) {
						// solo
						results.add(FindMyStoredDateResult.findByMyStoredDateSoloTest(query.getExhId(), dateInfoList));
					} else {
						// gather
						results.add(
							FindMyStoredDateResult.findByMyStoredDateGatherTest(query.getExhId(), gathering, dateInfoList));
					}
					checkGatherId = 0L;
					dateInfoList.clear();
				} else {
					dateInfoList.add(StoredDateInfo.builder()
						.userExhId(exhVisit.getExhVisitId())
						.visitDate(exhVisit.getVisitDate())
						.build());
				}
			}
		}

		// // 전시회 아이디 검증
		// ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
		// 	.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// // 전시회에 대한 개인의 일정 -> userExh 테이블
		// List<UserExhEntity> userExhEntities = userExhRepository.findByUserIdAndExhId(userId, exhEntity.getExhId());
		// if (!userExhEntities.isEmpty()) {
		// 	List<StoredDateInfo> dateInfoList = new ArrayList<>();
		// 	for (UserExhEntity userExh : userExhEntities) {
		// 		dateInfoList.add(StoredDateInfo.builder()
		// 			.userExhId(userExh.getUserExhId())
		// 			.visitDate(userExh.getVisitDate())
		// 			.build());
		// 	}
		// 	// getVisitDate 날짜 순으로 정렬
		// 	dateInfoList = dateInfoList.stream()
		// 		.sorted(Comparator.nullsLast(Comparator.comparing(StoredDateInfo::getVisitDate,
		// 			Comparator.nullsLast(Comparator.naturalOrder()))))
		// 		.collect(Collectors.toList());
		// 	results.add(FindMyStoredDateResult.findByMyStoredDateSolo(userExhEntities.getFirst(), dateInfoList));
		// }
		// // 자신이 속한 모임에 대한 한 전시회에 대한 일정 -> gatherMate, gatherExh 테이블
		// // 자신이 속한 모임 목록
		// List<GatheringMateEntity> gatheringMateEntities = gatheringMateRepository.findByGatheringMateIdUserId(userId);
		// // 전시회에 대한 모임의 일정
		// for (GatheringMateEntity gatheringMate : gatheringMateEntities) {
		// 	List<GatheringExhEntity> gatheringExhEntities = gatheringExhRepository.findByGatherIdAndExhId(
		// 		gatheringMate.getGatheringMateId().getGatherId(), exhEntity.getExhId());
		// 	if (!gatheringExhEntities.isEmpty()) {
		// 		GatheringEntity gathering = gatheringRepository.findByGatherId(
		// 				gatheringExhEntities.getFirst().getGatherId())
		// 			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 		List<StoredDateInfo> dateInfoList = new ArrayList<>();
		// 		for (GatheringExhEntity gatheringExh : gatheringExhEntities) {
		// 			dateInfoList.add(StoredDateInfo.builder()
		// 				.gatherExhId(gatheringExh.getGatherExhId())
		// 				.visitDate(gatheringExh.getVisitDate())
		// 				.build());
		// 		}
		// 		dateInfoList = dateInfoList.stream()
		// 			.sorted(Comparator.nullsLast(Comparator.comparing(StoredDateInfo::getVisitDate,
		// 				Comparator.nullsLast(Comparator.naturalOrder()))))
		// 			.collect(Collectors.toList());
		// 		results.add(
		// 			FindMyStoredDateResult.findByMyStoredDateGather(gatheringExhEntities.getFirst(), gathering,
		// 				dateInfoList));
		// 	}
		// }
		return results;
	}

	@Transactional
	@Override
	public List<FindMyStoredDateResult> addMyExhVisitDateDummy(AddMyExhVisitDateDummyCommand command) {
		// 전시회 아이디 검증
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 관람날짜 검증
		Optional<UserExhEntity> userExhEntity = userExhRepository.findByUserIdAndExhIdAndVisitDate(getUserId(),
			command.getExhId(), command.getVisitDate());

		if (userExhEntity.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 전시회 일정에 맞춰 갈 수 있는지 확인
		if (command.getVisitDate() != null) {
			if (exhEntity.getExhPeriodStart().isAfter(command.getVisitDate())
				|| exhEntity.getExhPeriodEnd().isBefore(command.getVisitDate())) {
				throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
			}
		}

		// DB에 데이터 생성
		UserExhEntity entity = UserExhEntity.builder()
			.visitDate(command.getVisitDate())
			.userId(getUserId())
			.exhId(command.getExhId())
			.build();
		userExhRepository.save(entity);

		List<UserExhEntity> entities = userExhRepository.findByUserIdAndExhId(getUserId(), command.getExhId());
		List<FindMyStoredDateResult> results = new ArrayList<>();

		for (UserExhEntity tmp : entities) {
			results.add(MyExhReadUseCase.FindMyStoredDateResult.findByMyAllDatesSolo(tmp));
		}
		return results;
		//	return MyExhsReadUseCase.FindMyStoredDateResult.findByMyStoredDateSolo(entity, null);
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
