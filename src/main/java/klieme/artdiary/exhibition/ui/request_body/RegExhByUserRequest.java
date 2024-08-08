package klieme.artdiary.exhibition.ui.request_body;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegExhByUserRequest {
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

	@NotNull
	Integer regFee;

	String regIntro;

	String regUrl;

	@NotBlank
	String regPoster;

	String regArt;

	@NotNull
	LocalDateTime regDate;
}
