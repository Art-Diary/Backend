package klieme.artdiary.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

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
