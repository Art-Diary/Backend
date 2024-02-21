package klieme.artdiary.gatherings.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddGatheringMateRequest {
	@NotNull
	private Long userId;
}
