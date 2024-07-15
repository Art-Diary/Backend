package klieme.artdiary.solo.ui.request_body;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddMyExhVisitDateRequest { //혜원 추가

	@NotNull
	public Long exhId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public LocalDate visitDate;

}
