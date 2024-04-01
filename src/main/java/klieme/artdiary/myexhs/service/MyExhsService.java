package klieme.artdiary.myexhs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;
import klieme.artdiary.myexhs.info.StoredDateInfo;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class MyExhsService implements MyExhsReadUseCase, MyExhsOperationUseCase {
	private final GatheringMateRepository gatheringMateRepository;
	private final GatheringRepository gatheringRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final UserExhRepository userExhRepository;
	private final ExhRepository exhRepository;
	private final MydiaryRepository mydiaryRepository;
	private final ImageTransfer imageTransfer;
	private final UserRepository userRepository;

	@Autowired
	public MyExhsService(GatheringMateRepository gatheringMateRepository, GatheringRepository gatheringRepository,
		GatheringExhRepository gatheringExhRepository, GatheringDiaryRepository gatheringDiaryRepository,
		UserExhRepository userExhRepository, ExhRepository exhRepository, MydiaryRepository mydiaryRepository,
		ImageTransfer imageTransfer, UserRepository userRepository) {
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringRepository = gatheringRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.userExhRepository = userExhRepository;
		this.exhRepository = exhRepository;
		this.mydiaryRepository = mydiaryRepository;
		this.imageTransfer = imageTransfer;
		this.userRepository = userRepository;
	}

	@Override
	public List<MyExhsReadUseCase.FindMyExhsResult> getMyExhsList() throws IOException {
		Long userId = getUserId();
		// 1. 개인의 전시회 기록
		List<Map<String, Object>> myStoredExhList = mydiaryRepository.sumRateByUserExhId(userId);
		// 2. gatheringDiary애서 userId를 통해 친구가 작성한 기록이 있는 경우
		List<Map<String, Object>> myStoredGatherExhList = mydiaryRepository.sumRateByGatherExhId(userId);

		//
		HashMap<Long, Long> countDiary = new HashMap<>(); // 전시회에 대한 기록 개수
		HashMap<Long, Double> sumDiaryRate = new HashMap<>(); // 전시회에 대한 기록 별점 합
		HashMap<Long, ExhEntity> exhEntityHashMap = new HashMap<>(); // 전시회에 대한 기록 별점 합

		// 중복 코드를 줄이기 위해 메서드로 분리
		processExhibitionList(myStoredExhList, countDiary, sumDiaryRate, exhEntityHashMap);
		processExhibitionList(myStoredGatherExhList, countDiary, sumDiaryRate, exhEntityHashMap);

		List<Long> exhIds = new ArrayList<>(countDiary.keySet());
		exhIds.sort(Comparator.naturalOrder());

		List<MyExhsReadUseCase.FindMyExhsResult> result = new ArrayList<>();
		for (Long exhId : exhIds) {
			ExhEntity exh = exhEntityHashMap.get(exhId);
			double averageRate = countDiary.get(exhId) == 0 ? 0.0 : sumDiaryRate.get(exhId) / countDiary.get(exhId);
			String poster = imageTransfer.downloadImage(exh.getPoster());
			result.add(MyExhsReadUseCase.FindMyExhsResult.findMyExhs(exh, averageRate, poster));
		}
		return result;
	}

	@Override
	public List<FindMyStoredDateResult> getStoredDateOfExhs(MyStoredDateFindQuery query) {
		Long userId = getUserId();
		List<FindMyStoredDateResult> results = new ArrayList<>();

		// 전시회 아이디 검증
		ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 전시회에 대한 개인의 일정 -> userExh 테이블
		List<UserExhEntity> userExhEntities = userExhRepository.findByUserIdAndExhId(userId, exhEntity.getExhId());
		if (!userExhEntities.isEmpty()) {
			List<StoredDateInfo> dateInfoList = new ArrayList<>();
			for (UserExhEntity userExh : userExhEntities) {
				dateInfoList.add(StoredDateInfo.builder()
					.userExhId(userExh.getUserExhId())
					.visitDate(userExh.getVisitDate())
					.build());
			}
			dateInfoList = dateInfoList.stream()
				.sorted(Comparator.nullsLast(Comparator.comparing(StoredDateInfo::getVisitDate,
					Comparator.nullsLast(Comparator.naturalOrder()))))
				.collect(Collectors.toList());
			results.add(FindMyStoredDateResult.findByMyStoredDateSolo(userExhEntities.getFirst(), dateInfoList));
		}
		// 자신이 속한 모임에 대한 한 전시회에 대한 일정 -> gatherMate, gatherExh 테이블
		// 자신이 속한 모임 목록
		List<GatheringMateEntity> gatheringMateEntities = gatheringMateRepository.findByGatheringMateIdUserId(userId);
		// 전시회에 대한 모임의 일정
		for (GatheringMateEntity gatheringMate : gatheringMateEntities) {
			List<GatheringExhEntity> gatheringExhEntities = gatheringExhRepository.findByGatherIdAndExhId(
				gatheringMate.getGatheringMateId().getGatherId(), exhEntity.getExhId());
			if (!gatheringExhEntities.isEmpty()) {
				GatheringEntity gathering = gatheringRepository.findByGatherId(
						gatheringExhEntities.getFirst().getGatherId())
					.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
				List<StoredDateInfo> dateInfoList = new ArrayList<>();
				for (GatheringExhEntity gatheringExh : gatheringExhEntities) {
					dateInfoList.add(StoredDateInfo.builder()
						.gatheringExhId(gatheringExh.getGatheringExhId())
						.visitDate(gatheringExh.getVisitDate())
						.build());
				}
				dateInfoList = dateInfoList.stream()
					.sorted(Comparator.nullsLast(Comparator.comparing(StoredDateInfo::getVisitDate,
						Comparator.nullsLast(Comparator.naturalOrder()))))
					.collect(Collectors.toList());
				results.add(
					FindMyStoredDateResult.findByMyStoredDateGather(gatheringExhEntities.getFirst(), gathering,
						dateInfoList));
			}
		}
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
			results.add(MyExhsReadUseCase.FindMyStoredDateResult.findByMyAllDatesSolo(tmp));
		}
		return results;
		//	return MyExhsReadUseCase.FindMyStoredDateResult.findByMyStoredDateSolo(entity, null);
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

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}