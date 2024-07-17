package klieme.artdiary.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.user.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenView {
	private final String accessToken;

	@Builder
	public AccessTokenView(UserReadUseCase.FindAccessTokenResult result) {
		this.accessToken = result.getAccessToken();
	}
}
