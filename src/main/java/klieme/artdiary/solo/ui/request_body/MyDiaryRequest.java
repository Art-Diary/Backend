package klieme.artdiary.solo.ui.request_body;

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
public class MyDiaryRequest {
	@NotNull
	private Long exhVisitId;
	@NotBlank
	private String title;
	@NotNull
	private Double rate;
	@NotNull
	private Boolean diaryPrivate;
	@NotBlank
	private String contents;
	private MultipartFile thumbnail;
	private MultipartFile[] files;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate writeDate;
	@NotBlank
	private String saying;
}
