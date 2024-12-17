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
	private final Boolean favoriteExhAlarm;
	private final Boolean visitSoloAlarm;
	private final Boolean visitGatheringAlarm;
	private final Boolean newGatheringAlarm;
	private final Boolean newDateGatheringAlarm;
	private final Boolean initInfo;
	private final String providerType;
	private final String accessToken;
	private final String roleType;

	@Builder
	public UserView(UserReadUseCase.FindUserResult result) {
		this.userId = result.getUserId();
		this.email = result.getEmail();
		this.profile = result.getProfile();
		this.favoriteArt = result.getFavoriteArt();
		this.nickname = result.getNickname();
		this.favoriteExhAlarm = result.getFavoriteExhAlarm();
		this.visitSoloAlarm = result.getVisitSoloAlarm();
		this.visitGatheringAlarm = result.getVisitGatheringAlarm();
		this.newGatheringAlarm = result.getNewGatheringAlarm();
		this.newDateGatheringAlarm = result.getNewDateGatheringAlarm();
		this.initInfo = result.getInitInfo();
		this.providerType = result.getProviderType();
		this.accessToken = result.getAccessToken();
		this.roleType = result.getRoleType();
	}
}
