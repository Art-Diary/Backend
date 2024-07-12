package klieme.artdiary.user.ui.request_body;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class AlarmTokenRequest {
	@NotBlank
	String alarmToken;
}
