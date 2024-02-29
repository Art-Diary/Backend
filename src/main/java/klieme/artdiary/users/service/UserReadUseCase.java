package klieme.artdiary.users.service;

import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface UserReadUseCase {
	@Getter
	@ToString
	@Builder
	class FindUserResult {
		private final Long userId;
		private final String nickname;
		private final String email;
		private final String profile;
		private final String favoriteArt;

		public static FindUserResult findByUser(UserEntity user, String profile) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.email(user.getEmail())
				.profile(profile)
				.favoriteArt(user.getFavoriteArt())
				.build();
		}
	}
}
