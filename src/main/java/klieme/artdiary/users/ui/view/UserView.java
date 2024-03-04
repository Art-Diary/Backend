package klieme.artdiary.users.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.users.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserView {
	private final Long userId;
	private final String nickname;
	private final String email;
	private final String profile;
	private final String favoriteArt; //⇒ profile: byte로 변환된 이미지가 string 형식으로 전달됨.

	@Builder
	public UserView(UserReadUseCase.FindUserResult result) {
		this.userId = result.getUserId();
		this.email = result.getEmail();
		this.profile = result.getProfile();
		this.favoriteArt = result.getFavoriteArt();
		this.nickname = result.getNickname();
	}
}
