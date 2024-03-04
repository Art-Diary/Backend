package klieme.artdiary.users.ui.request_body;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserNicknameRequest {

	@NotBlank
	private String nickname;

}
