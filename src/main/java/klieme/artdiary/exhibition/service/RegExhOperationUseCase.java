package klieme.artdiary.exhibition.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface RegExhOperationUseCase {
	RegExhReadUseCase.FindRegExhResult createRegExhByUser(RegExhCreateUpdateByUserCommand command);

	RegExhReadUseCase.FindRegExhResult updateRegExhByUser(RegExhCreateUpdateByUserCommand command);

	void deleteRegExhByUser(Long regExhId);

	RegExhReadUseCase.FindRegExhResult confirmExhRequestByAdmin(RegExhUpdateByAdminCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class RegExhCreateUpdateByUserCommand {
		private final Long regExhId;
		private final String regExhName;
		private final String regGallery;
		private final LocalDate regExhPeriodStart;
		private final LocalDate regExhPeriodEnd;
		private final String regPainter;
		private final Integer regFee;
		private final String regIntro;
		private final String regUrl;
		private final String regPoster;
		private final String regArt;
		private final LocalDateTime regDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class RegExhUpdateByAdminCommand {
		private final Long regExhId;
		private final String regExhName;
		private final String regGallery;
		private final LocalDate regExhPeriodStart;
		private final LocalDate regExhPeriodEnd;
		private final String regPainter;
		private final Integer regFee;
		private final String regIntro;
		private final String regUrl;
		private final String regPoster;
		private final String regArt;
		private final String regComment;
	}
}
