package klieme.artdiary.mate.service;

import java.io.IOException;
import java.util.List;

import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface MateReadUseCase {
	List<FindMateResult> getMateList() throws IOException;

	List<FindMateResult> searchNewMate(String nickname) throws IOException;

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
