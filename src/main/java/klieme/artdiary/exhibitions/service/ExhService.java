package klieme.artdiary.exhibitions.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;

@Service
public class ExhService implements ExhOperationUseCase, ExhReadUseCase {

	private final ExhRepository exhRepository;
	private final UserExhRepository userExhRepository;

	@Autowired
	public ExhService(ExhRepository exhRepository, UserExhRepository userExhRepository) {
		this.exhRepository = exhRepository;
		this.userExhRepository = userExhRepository;
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
		}
		/* TODO
		 * (목적) 한 전시회에 대한 캘린더에 저장된 모임의 일정 날짜 조회 로직 구현 필요
		 */
		return FindStoredDateResult.findByStoredDate(query.getExhId(), null, dates);
	}

	private Long getUserId() {
		return 4L;
	}
}
