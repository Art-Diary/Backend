package klieme.artdiary.mate.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MateRequest {

	@NotNull
	public Long userId;
}
