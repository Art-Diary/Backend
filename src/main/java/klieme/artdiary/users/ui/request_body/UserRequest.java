package klieme.artdiary.users.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserRequest {
	@NotNull
	private String email;
	@NotNull
	private String nickname;
	@NotNull
	private String profile;
	@NotNull
	private String providerType;
	@NotNull
	private String providerId;
	@NotNull
	private String favoriteArt;
	@NotNull
	private Boolean alarm1;
	@NotNull
	private Boolean alarm2;
	@NotNull
	private Boolean alarm3;
}
