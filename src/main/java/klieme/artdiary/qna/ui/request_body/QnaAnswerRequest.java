package klieme.artdiary.qna.ui.request_body;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaAnswerRequest {
	@NotBlank
	private String answer;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate answerDate;
}
