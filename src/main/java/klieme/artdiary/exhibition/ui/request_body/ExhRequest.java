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
public class ExhRequest {

	@NotBlank
	private String exhName;

	@NotBlank
	private String gallery;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate exhPeriodStart;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate exhPeriodEnd;

	private String painter;

	@NotNull
	private Integer fee;

	private String intro;

	private String url;

	@NotNull
	private MultipartFile poster;

	@NotBlank
	private String art;

	@NotBlank
	private String source;
}
