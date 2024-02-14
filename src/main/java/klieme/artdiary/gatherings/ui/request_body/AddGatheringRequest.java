package klieme.artdiary.gatherings.ui.request_body;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddGatheringRequest {
	@NotBlank
	private String gatherName;
}
