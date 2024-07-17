package klieme.artdiary.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.user.service.UserReadUseCase;
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
	private final String profile; //⇒ profile: byte로 변환된 이미지가 string 형식으로 전달됨.
	private final String favoriteArt;
	private final Boolean alarm1;
	private final Boolean alarm2;
	private final Boolean alarm3;
	private final Boolean initInfo;
	private final String providerType;
	private final String accessToken;

	@Builder
	public UserView(UserReadUseCase.FindUserResult result) {
		this.userId = result.getUserId();
		this.email = result.getEmail();
		this.profile = result.getProfile();
		this.favoriteArt = result.getFavoriteArt();
		this.nickname = result.getNickname();
		this.alarm1 = result.getAlarm1();
		this.alarm2 = result.getAlarm2();
		this.alarm3 = result.getAlarm3();
		this.initInfo = result.getInitInfo();
		this.providerType = result.getProviderType();
		this.accessToken = result.getAccessToken();
	}
}
