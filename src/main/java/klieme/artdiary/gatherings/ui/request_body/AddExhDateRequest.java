package klieme.artdiary.gatherings.ui.request_body;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddExhDateRequest {
	@NotNull
	private Long exhId;
	@NotNull
	private LocalDate visitDate;
}
