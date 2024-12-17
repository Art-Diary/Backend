package klieme.artdiary.exhibition.ui.request_body;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class RegExhByUserRequest {
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

	@NotNull
	Integer regFee;

	String regUrl;

	MultipartFile regPoster;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime regDate;
}
