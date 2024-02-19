package klieme.artdiary.mate.service;

import java.util.List;

import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface MateReadUseCase {
	List<FindMateResult> getMateList();

	@Getter
	@ToString
	@Builder
	class FindMateResult {
		private final Long userId;
		private final String nickname;
		private final String profile;
		private final String favoriteArt;

		public static FindMateResult findByGatheringExhs(UserEntity user, String profile) {
			return FindMateResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(profile)
				.favoriteArt(user.getFavoriteArt())
				.build();
		}
	}
}
