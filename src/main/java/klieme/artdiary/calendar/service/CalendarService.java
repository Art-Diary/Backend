package klieme.artdiary.calendar.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.calendar.info.ScheduleInfo;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;

@Service
public class CalendarService implements CalendarReadUseCase {
	private final UserExhRepository userExhRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final ExhRepository exhRepository;
	private final GatheringRepository gatheringRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public CalendarService(UserExhRepository userExhRepository, GatheringMateRepository gatheringMateRepository,
		GatheringExhRepository gatheringExhRepository, ExhRepository exhRepository,
		GatheringRepository gatheringRepository, ImageTransfer imageTransfer) {
		this.userExhRepository = userExhRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.exhRepository = exhRepository;
		this.gatheringRepository = gatheringRepository;
		this.imageTransfer = imageTransfer;
	}

	@Override
	public List<FindCalendarResult> getExhSchedule(CalendarFindQuery query) throws IOException {
		// 반환 리스트
		List<FindCalendarResult> results = new ArrayList<>();
		// 전시회 정보 디비 요청 줄이기 위해 저장
		List<Pair<Long, ExhEntity>> exhInfo = new ArrayList<>();
		// FindCalendarTestResult의 dayOfScheduleInfos 값 구하기
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos = new HashMap<>();
		// 날짜 비교 중 월 시작 날짜와 마지막 날짜 구하기
		LocalDate visitDateStart = LocalDate.of(query.getYear(), query.getMonth(), 1);
		LocalDate visitDateEnd = visitDateStart.withDayOfMonth(visitDateStart.lengthOfMonth());

		if (query.getKind() == CalendarKind.ALONE) { // 개인일 경우
			getAloneCalendar(visitDateStart, visitDateEnd, dayOfScheduleInfos, exhInfo);
		} else if (query.getKind() == CalendarKind.GATHER) { // 모임일 경우
			getOneGatherCalender(query.getGatherId(), visitDateStart, visitDateEnd, dayOfScheduleInfos, exhInfo);
		} else { // 전체일 경우
			getAloneCalendar(visitDateStart, visitDateEnd, dayOfScheduleInfos, exhInfo);
			getGathersCalendar(visitDateStart, visitDateEnd, dayOfScheduleInfos, exhInfo);
		}
		for (int day = 1; day <= visitDateEnd.getDayOfMonth(); day++) {
			if (dayOfScheduleInfos.get(day) != null) {
				results.add(FindCalendarResult.findByCalendar(day, dayOfScheduleInfos.get(day)));
			} else {
				results.add(FindCalendarResult.findByCalendar(day, null));
			}
		}
		return results;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}

	private ExhEntity checkExhInfo(List<Pair<Long, ExhEntity>> exhInfo, Long exhId) {
		Optional<Pair<Long, ExhEntity>> getPair = exhInfo.stream()
			.filter(x -> x.getFirst().equals(exhId))
			.findAny();
		ExhEntity exh;

		if (getPair.isPresent()) {
			exh = getPair.get().getSecond();
		} else {
			Optional<ExhEntity> exhEntityOptional = exhRepository.findByExhId(exhId);

			if (exhEntityOptional.isEmpty()) {
				return null;
			}
			exh = exhEntityOptional.get();
			exhInfo.add(Pair.of(exh.getExhId(), exh));
		}
		return exh;
	}

	private void fillDayOfExhInfo(List<Pair<Long, ExhEntity>> exhInfo, UserExhEntity userExh,
		GatheringExhEntity gatheringExh, HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos) throws IOException {
		long exhId = userExh != null ? userExh.getExhId() : gatheringExh.getExhId();
		LocalDate visitDate = userExh != null ? userExh.getVisitDate() : gatheringExh.getVisitDate();
		ExhEntity exh = checkExhInfo(exhInfo, exhId);

		if (exh != null) {
			int day = visitDate.getDayOfMonth();
			String poster = imageTransfer.downloadImage(exh.getPoster());

			dayOfScheduleInfos.computeIfAbsent(day, k -> new ArrayList<>());
			dayOfScheduleInfos.get(day).add(ScheduleInfo.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(poster)
				.visitDate(visitDate)
				.build());
		}
	}

	private void getAloneCalendar(LocalDate visitDateStart, LocalDate visitDateEnd,
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos, List<Pair<Long, ExhEntity>> exhInfo) throws
		IOException {
		// date와 userId로 userExh 조회
		List<UserExhEntity> userExhEntityList = userExhRepository.findByUserIdAndVisitDateBetween(getUserId(),
			visitDateStart, visitDateEnd);

		for (UserExhEntity userExh : userExhEntityList) {
			fillDayOfExhInfo(exhInfo, userExh, null, dayOfScheduleInfos);
		}
	}

	private void getOneGatherCalender(Long gatherId, LocalDate visitDateStart, LocalDate visitDateEnd,
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos, List<Pair<Long, ExhEntity>> exhInfo) throws
		IOException {
		// 속한 모임 목록 조회
		Optional<GatheringMateEntity> gatheringMate = gatheringMateRepository.findByGatheringMateId(
			GatheringMateId.builder()
				.gatherId(gatherId)
				.userId(getUserId())
				.build());

		if (gatheringMate.isPresent()) {
			Optional<GatheringEntity> gathering = gatheringRepository.findByGatherId(gatherId);

			if (gathering.isPresent()) {
				List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherIdAndVisitDateBetween(
					gathering.get().getGatherId(), visitDateStart, visitDateEnd);
				for (GatheringExhEntity gatheringExh : gatheringExhEntityList) {
					fillDayOfExhInfo(exhInfo, null, gatheringExh, dayOfScheduleInfos);
				}
			}
		}
	}

	private void getGathersCalendar(LocalDate visitDateStart, LocalDate visitDateEnd,
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos, List<Pair<Long, ExhEntity>> exhInfo) throws
		IOException {
		// 속한 모임 목록 조회
		List<GatheringMateEntity> gatheringMateEntityList = gatheringMateRepository.findByGatheringMateIdUserId(
			getUserId());

		for (GatheringMateEntity gatheringMate : gatheringMateEntityList) {
			Optional<GatheringEntity> gathering = gatheringRepository.findByGatherId(
				gatheringMate.getGatheringMateId().getGatherId());

			if (gathering.isEmpty()) {
				continue;
			}
			List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherIdAndVisitDateBetween(
				gathering.get().getGatherId(), visitDateStart, visitDateEnd);
			for (GatheringExhEntity gatheringExh : gatheringExhEntityList) {
				fillDayOfExhInfo(exhInfo, null, gatheringExh, dayOfScheduleInfos);
			}
		}
	}
}
