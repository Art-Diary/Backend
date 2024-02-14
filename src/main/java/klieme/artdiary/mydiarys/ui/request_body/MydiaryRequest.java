package klieme.artdiary.mydiarys.ui.request_body;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MydiaryRequest {
	@NotNull
	private Long userExhId; // 개인 일정이 아닌 경우 -1
	@NotNull
	private Long gatheringExhId; // 모임이 아닐 경우 -1
	@NotBlank
	private String title;
	@NotNull
	private Double rate;
	@NotNull
	private Boolean diaryPrivate;
	@NotBlank
	private String contents;
	private String thumbnail;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate writeDate;
	private String saying;
}

