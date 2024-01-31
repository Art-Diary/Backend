package klieme.artdiary.exhibitions.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query) {
        // 캘린더에 개인이 저장한 전시회 관람 날짜
        // userId: getUserId(), exhId: query.getExhId()
        Long userId = getUserId();
        List<Date> dates = new ArrayList<>();

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
