package klieme.artdiary.exhibitions.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

public interface ExhOperationUseCase {

    String createDummy(klieme.artdiary.exhibitions.service.ExhOperationUseCase.ExhDummyCreateCommand command);

    @EqualsAndHashCode
    @Builder
    @Getter
    @ToString
    class ExhDummyCreateCommand {
        private final String exhName;
        private final String gallery;
        private final Date exhPeriodStart;
        private final Date exhPeriodEnd;
        private final String painter;
        private final Integer fee;
        private final String intro;
        private final String url;
        private final String poster;

    }
}
