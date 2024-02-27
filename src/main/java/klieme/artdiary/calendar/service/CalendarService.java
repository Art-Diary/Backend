package klieme.artdiary.calendar.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import klieme.artdiary.calendar.enums.CalendarKind;
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

	@Autowired
	public CalendarService(UserExhRepository userExhRepository, GatheringMateRepository gatheringMateRepository,
		GatheringExhRepository gatheringExhRepository, ExhRepository exhRepository,
		GatheringRepository gatheringRepository) {
		this.userExhRepository = userExhRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.exhRepository = exhRepository;
		this.gatheringRepository = gatheringRepository;
	}

	@Override
	public List<FindCalendarResult> getExhSchedule(CalendarFindQuery query) {
		// 반환 리스트
		List<FindCalendarResult> results = new ArrayList<>();
		// 전시회 정보 디비 요청 줄이기 위해 저장
		List<Pair<Long, ExhEntity>> exhInfo = new ArrayList<>();

		if (query.getKind() == CalendarKind.ALONE) { // 개인일 경우
			getAloneCalendar(query.getDate(), results, exhInfo);
		} else if (query.getKind() == CalendarKind.GATHER) { // 모임일 경우
			getOneGatherCalender(query.getDate(), results, query.getGatherId(), exhInfo);
		} else { // 전체일 경우
			getAloneCalendar(query.getDate(), results, exhInfo);
			getGathersCalendar(query.getDate(), results, exhInfo);
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

	private void getAloneCalendar(LocalDate visitDate, List<FindCalendarResult> results,
		List<Pair<Long, ExhEntity>> exhInfo) {
		// date와 userId로 userExh 조회
		List<UserExhEntity> userExhEntityList = userExhRepository.findByUserIdAndVisitDate(getUserId(), visitDate);

		for (UserExhEntity userExh : userExhEntityList) {
			ExhEntity exh = checkExhInfo(exhInfo, userExh.getExhId());

			if (exh == null) {
				continue;
			}
			results.add(FindCalendarResult.findByCalendar(exh, visitDate, null, GatheringEntity.builder().build()));
		}
	}

	private void getOneGatherCalender(LocalDate visitDate, List<FindCalendarResult> results, Long gatherId,
		List<Pair<Long, ExhEntity>> exhInfo) {
		// 속한 모임 목록 조회
		Optional<GatheringMateEntity> gatheringMate = gatheringMateRepository.findByGatheringMateId(
			GatheringMateId.builder()
				.gatherId(gatherId)
				.userId(getUserId())
				.build());

		if (gatheringMate.isPresent()) {
			Optional<GatheringEntity> gathering = gatheringRepository.findByGatherId(gatherId);

			if (gathering.isPresent()) {
				List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherIdAndVisitDate(
					gathering.get().getGatherId(), visitDate);
				for (GatheringExhEntity gatheringExh : gatheringExhEntityList) {
					ExhEntity exh = checkExhInfo(exhInfo, gatheringExh.getExhId());

					if (exh == null) {
						continue;
					}
					results.add(FindCalendarResult.findByCalendar(exh, visitDate, null, gathering.get()));
				}
			}
		}
	}

	private void getGathersCalendar(LocalDate visitDate, List<FindCalendarResult> results,
		List<Pair<Long, ExhEntity>> exhInfo) {
		// 속한 모임 목록 조회
		List<GatheringMateEntity> gatheringMateEntityList = gatheringMateRepository.findByGatheringMateIdUserId(
			getUserId());

		for (GatheringMateEntity gatheringMate : gatheringMateEntityList) {
			Optional<GatheringEntity> gathering = gatheringRepository.findByGatherId(
				gatheringMate.getGatheringMateId().getGatherId());

			if (gathering.isEmpty()) {
				continue;
			}
			List<GatheringExhEntity> gatheringExhEntityList = gatheringExhRepository.findByGatherIdAndVisitDate(
				gathering.get().getGatherId(), visitDate);
			for (GatheringExhEntity gatheringExh : gatheringExhEntityList) {
				ExhEntity exh = checkExhInfo(exhInfo, gatheringExh.getExhId());

				if (exh == null) {
					continue;
				}
				results.add(FindCalendarResult.findByCalendar(exh, visitDate, null, gathering.get()));
			}
		}
	}
}
