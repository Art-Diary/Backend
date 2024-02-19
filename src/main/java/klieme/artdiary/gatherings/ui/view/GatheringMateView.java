package klieme.artdiary.gatherings.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringMateView {
	private final Long userId;
	private final String nickname;
	private final String profile;
	private final String favoriteArt;

	@Builder
	public GatheringMateView(GatheringReadUseCase.FindGatheringMatesResult result) {
		this.userId = result.getUserId();
		this.nickname = result.getNickname();
		this.profile = result.getProfile();
		this.favoriteArt = result.getFavoriteArt();
	}
}
