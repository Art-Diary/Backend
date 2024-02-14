package klieme.artdiary.exhibitions.ui.request_body;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExhRequest {

	@NotBlank
	private String exhName;

	@NotBlank
	private String gallery;

	@NotNull
	private LocalDate exhPeriodStart;

	@NotNull
	private LocalDate exhPeriodEnd;

	@NotBlank
	private String painter;

	@NotNull
	private Integer fee;

	@NotBlank
	private String intro;

	@NotBlank
	private String url;

	@NotBlank
	private String poster;
}
