package klieme.artdiary.user.ui.request_body;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserRequest {
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String providerType;
	@NotBlank
	private String providerId;
	private String alarmToken;
}
