package klieme.artdiary.exhibition.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import klieme.artdiary.exhibition.enums.RegExhState;
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
		private final Integer regFee;
		private final String regUrl;
		private final MultipartFile regPoster;
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
		private final MultipartFile regPoster;
		private final String regArt;
		private final String regComment;
		private final RegExhState regState;
		private final String regSource;
	}
}
