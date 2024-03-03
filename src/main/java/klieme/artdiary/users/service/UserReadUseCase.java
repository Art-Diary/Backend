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

	@Getter
	@ToString
	@Builder
	class FindAlarmResult {
		private final Boolean alarm1;
		private final Boolean alarm2;
		private final Boolean alarm3;

		@Builder
		public static FindAlarmResult findAlarm1(UserEntity user) {
			return FindAlarmResult.builder()
				.alarm1(user.getAlarm1())
				.build();
		}

		@Builder
		public static FindAlarmResult findAlarm2(UserEntity user) {
			return FindAlarmResult.builder()
				.alarm2(user.getAlarm2())
				.build();
		}

		@Builder
		public static FindAlarmResult findAlarm3(UserEntity user) {
			return FindAlarmResult.builder()
				.alarm3(user.getAlarm3())
				.build();
		}
	}
}
