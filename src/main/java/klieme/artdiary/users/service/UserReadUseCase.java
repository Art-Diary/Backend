package klieme.artdiary.users.service;

import java.io.IOException;

import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface UserReadUseCase {

	FindUserResult getUserInfo() throws IOException;

	@Getter
	@ToString
	@Builder
	class FindUserResult {
		private final String nickname;
		private final Long userId;
		private final String email;
		private final String profile;
		private final String favoriteArt;

		public static FindUserResult findUserInfo(UserEntity user, String profile) {
			return FindUserResult.builder()
				.nickname(user.getNickname())
				.userId(user.getUserId())
				.email(user.getEmail())
				.profile(profile)
				.favoriteArt(user.getFavoriteArt()).build();
		}
	}
}
