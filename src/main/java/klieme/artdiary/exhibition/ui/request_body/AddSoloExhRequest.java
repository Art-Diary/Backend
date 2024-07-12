package klieme.artdiary.exhibition.ui.request_body;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddSoloExhRequest {

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public LocalDate visitDate;

}
