package klieme.artdiary.exhibitions.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<UserExhEntity> userexhEntity = userExhRepository.findByUserIdAndExhIdAndVisitDate(getUserId(),
            command.getExhId(), command.getVisitDate());

        if (userexhEntity.isPresent()) {
            throw new ArtDiaryException(MessageType.CONFLICT);
        }
        // DB에 데이터 생성
        UserExhEntity entity = UserExhEntity.builder()
                .visitDate(command.getVisitDate())
                .userId(getUserId())
                .exhId(command.getExhId())
                .build();
        userExhRepository.save(entity);
        return FindStoredDateResult.findByStoredDate(command.getExhId(),command.getVisitDate(), null);
    }

    @Override
    public FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query) {
        // 캘린더에 개인이 저장한 전시회 관람 날짜
        // userId: getUserId(), exhId: query.getExhId()
        Long userId = getUserId();
        List<LocalDate> dates = new ArrayList<>();

        // 전시회 아이디 검증
        ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
            .orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

        if (query.getGatherId() == null) {
            List<UserExhEntity> entities = userExhRepository.findByUserIdAndExhId(userId, query.getExhId());
            for (UserExhEntity entity : entities) {
                dates.add(entity.getVisitDate());
            }
        }
        // 캘린더에 모임이 저장한 전시회 관람 날짜
        return FindStoredDateResult.findByStoredDate(query.getExhId(), null, dates);
    }

    private Long getUserId() {
        return 4L;
    }
}
