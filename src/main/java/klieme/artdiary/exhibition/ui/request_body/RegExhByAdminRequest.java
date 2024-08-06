package klieme.artdiary.exhibition.ui.request_body;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegExhByAdminRequest {
	@NotBlank
	String regExhName;

	@NotBlank
	String regGallery;

	@NotNull
	LocalDate regExhPeriodStart;

	@NotNull
	LocalDate regExhPeriodEnd;

	@NotBlank
	String regPainter;

	@NotBlank
	Integer regFee;

	String regIntro;

	String regUrl;

	@NotBlank
	String regPoster;

	String regArt;

	String regComment;
}
