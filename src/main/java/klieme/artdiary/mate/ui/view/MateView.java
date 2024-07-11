package klieme.artdiary.mate.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.mate.service.MateReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateView {
	private final Long userId;
	private final String nickname;
	private final String profile;
	private final String favoriteArt;

	@Builder
	public MateView(MateReadUseCase.FindMateResult result) {
		this.userId = result.getUserId();
		this.nickname = result.getNickname();
		this.profile = result.getProfile();
		this.favoriteArt = result.getFavoriteArt();
	}
}
