package klieme.artdiary.users.ui.view;

import klieme.artdiary.users.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserView {
	private final Long userId;
	private final String nickname;
	private final String email;
	private final String profile;
	private final String favoriteArt;

	@Builder
	public UserView(UserReadUseCase.FindUserResult result) {
		this.userId = result.getUserId();
		this.nickname = result.getNickname();
		this.email = result.getEmail();
		this.profile = result.getProfile();
		this.favoriteArt = result.getFavoriteArt();
	}
}
