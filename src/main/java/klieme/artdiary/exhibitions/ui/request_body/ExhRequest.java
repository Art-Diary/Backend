package klieme.artdiary.exhibitions.ui.request_body;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ExhRequest {

	@NotNull
	private String exhName;

	@NotNull
	private String gallery;

	@NotNull
	private LocalDate exhPeriodStart;

	@NotNull
	private LocalDate exhPeriodEnd;

	@NotNull
	private String painter;

	@NotNull
	private Integer fee;

	@NotNull
	private String intro;

	@NotNull
	private String url;

	@NotNull
	private String poster;
}
