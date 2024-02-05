package klieme.artdiary.gatherings.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddGatheringRequest {
	@NotNull
	private String gatherName;
}
