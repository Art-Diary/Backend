package klieme.artdiary.mate.service;

import java.util.List;

import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface MateReadUseCase {
	List<FindMateResult> getMateList();

	FindIsMateResult searchNewMate(String nickname);

	@Getter
	@ToString
	@Builder
	class FindIsMateResult {
		private final List<FindMateResult> alreadyMate;
		private final List<FindMateResult> notMate;

		public static FindIsMateResult findByGatheringMate(List<FindMateResult> alreadyMate,
			List<FindMateResult> notMate) {
			return FindIsMateResult.builder()
				.alreadyMate(alreadyMate)
				.notMate(notMate)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindMateResult {
		private final Long userId;
		private final String nickname;
		private final String profile;
		private final String favoriteArt;

		public static FindMateResult findByGatheringExhs(UserEntity user) {
			return FindMateResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(user.getProfile())
				.favoriteArt(user.getFavoriteArt())
				.build();
		}
	}
}
