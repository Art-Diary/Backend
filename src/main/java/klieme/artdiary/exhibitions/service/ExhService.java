package klieme.artdiary.exhibitions.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;

@Service
public class ExhService implements ExhOperationUseCase, ExhReadUseCase {

	private final ExhRepository exhRepository;
	private final UserExhRepository userExhRepository;
	private final GatheringMateRepository gatheringMateRepository;
	private final GatheringExhRepository gatheringExhRepository;

	@Autowired
	public ExhService(ExhRepository exhRepository, UserExhRepository userExhRepository,
		GatheringMateRepository gatheringMateRepository, GatheringExhRepository gatheringExhRepository) {
		this.exhRepository = exhRepository;
		this.userExhRepository = userExhRepository;
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringExhRepository = gatheringExhRepository;
	}

	@Override
	public String createDummy(ExhOperationUseCase.ExhDummyCreateCommand command) {
		ExhEntity entity = ExhEntity.builder()
			.exhName(command.getExhName())
			.gallery(command.getGallery())
			.exhPeriodStart(command.getExhPeriodStart())
			.exhPeriodEnd(command.getExhPeriodEnd())
			.painter(command.getPainter())
			.fee(command.getFee())
			.intro(command.getIntro())
			.url(command.getUrl())
			.poster(command.getPoster())
			.build();
		exhRepository.save(entity);
		return "complete";
	}

	@Override
	public FindStoredDateResult addSoloExhCreateDummy(ExhOperationUseCase.AddSoloExhDummyCreateCommand command) {
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
		if (exhEntity.getExhPeriodStart().isAfter(command.getVisitDate())
			|| exhEntity.getExhPeriodEnd().isBefore(command.getVisitDate())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
		}

		// DB에 데이터 생성
		UserExhEntity entity = UserExhEntity.builder()
			.visitDate(command.getVisitDate())
			.userId(getUserId())
			.exhId(command.getExhId())
			.build();
		userExhRepository.save(entity);
		return FindStoredDateResult.findByStoredDate(command.getExhId(), command.getVisitDate(), null);
	}

	@Override
	public FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query) {
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<LocalDate> dates = new ArrayList<>();

		// 전시회 아이디 검증
		exhRepository.findByExhId(query.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (query.getGatherId() == null) {
			// (목적) 한 전시회에 대한 캘린더에 저장된 개인의 일정 날짜 조회 로직 구현
			List<UserExhEntity> entities = userExhRepository.findByUserIdAndExhId(userId, query.getExhId());
			for (UserExhEntity entity : entities) {
				if (entity.getVisitDate() == null) { // 날짜 모름일 때는 건너뜀.
					continue;
				}
				dates.add(entity.getVisitDate());
			}
		} else {
			// (목적) 한 전시회에 대한 캘린더에 저장된 특정 모임의 일정 날짜 조회 로직 구현
			// gatherId와 userId로 유저가 모임에 포함되어있는지 확인 => gathering_mate 엔티티 필요
			gatheringMateRepository.findByGatheringMateId(GatheringMateId.builder()
					.userId(userId)
					.gatherId(query.getGatherId())
					.build())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			// 확인됐으면 gatherId로 전시회 exhId에 대해 저장된 날짜 가져오기 => gatheringExh 엔티티 필요
			List<GatheringExhEntity> entities = gatheringExhRepository.findByGatherIdAndExhId(query.getGatherId(),
				query.getExhId());
			for (GatheringExhEntity entity : entities) {
				if (entity.getVisitDate() == null) { // 날짜 모름일 때는 건너뜀.
					continue;
				}
				dates.add(entity.getVisitDate());
			}
		}
		return FindStoredDateResult.findByStoredDate(query.getExhId(), null, dates);
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
