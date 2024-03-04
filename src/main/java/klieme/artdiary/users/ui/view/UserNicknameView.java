package klieme.artdiary.users.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.users.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNicknameView {
	private final String nickname;

	@Builder
	public UserNicknameView(String nickname) {
		this.nickname = nickname;
	}
}
