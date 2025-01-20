package klieme.artdiary.exhibition.ui.request_body;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class RegExhByAdminRequest {
	@NotBlank
	String regExhName;

	@NotBlank
	String regGallery;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate regExhPeriodStart;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate regExhPeriodEnd;

	String regPainter;

	@NotNull
	Integer regFee;

	String regIntro;

	String regUrl;

	MultipartFile regPoster;

	@NotNull
	String regArt;

	String regComment;

	@NotBlank
	String regState;

	String regSource;
}
