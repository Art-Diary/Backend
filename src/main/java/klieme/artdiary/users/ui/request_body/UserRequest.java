package klieme.artdiary.users.ui.request_body;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	private String nickname;
	@NotBlank
	private String profile;
	@NotBlank
	private String providerType;
	@NotBlank
	private String providerId;
	@NotBlank
	private String favoriteArt;
	@NotNull
	private Boolean alarm1;
	@NotNull
	private Boolean alarm2;
	@NotNull
	private Boolean alarm3;
}
