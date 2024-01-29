package klieme.artdiary.exhibitions.service;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExhService implements ExhOperationUseCase{

    private final ExhRepository exhRepository;

    @Autowired
    public ExhService(ExhRepository exhRepository) {
        this.exhRepository = exhRepository;
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
}
