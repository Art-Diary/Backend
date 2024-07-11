package klieme.artdiary.user.service;

import java.io.IOException;
import java.util.Objects;

import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserReadUseCase {

	FindUserResult getUserInfo() throws IOException;

	String verifyNickname(UserReadUseCase.CreateNicknameCommand command);

	@Getter
	@ToString
	@Builder
	class FindUserResult {
		private final Long userId;
		private final String nickname;
		private final String email;
		private final String profile;
		private final String favoriteArt;
		private final Boolean alarm1;
		private final Boolean alarm2;
		private final Boolean alarm3;
		private final Boolean initInfo;
		private final String providerType;

		public static FindUserResult findUserInfo(UserEntity user, String profile) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.email(user.getEmail())
				.profile(profile)
				.favoriteArt(user.getFavoriteArt() == null || Objects.equals(user.getFavoriteArt(), ".") ? "그외" :
					user.getFavoriteArt())
				.alarm1(user.getAlarm1())
				.alarm2(user.getAlarm2())
				.alarm3(user.getAlarm3())
				.providerType(user.getProviderType())
				.build();
		}

		public static FindUserResult findUserLoginInfo(UserEntity user, Boolean initInfo, String profile) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.email(user.getEmail())
				.initInfo(initInfo)
				.nickname(initInfo ? user.getNickname() : null)
				.profile(initInfo ? profile : null)
				.favoriteArt(
					initInfo ? (user.getFavoriteArt() == null || Objects.equals(user.getFavoriteArt(), ".") ? "그외" :
						user.getFavoriteArt()) : null)
				.alarm1(initInfo ? user.getAlarm1() : null)
				.alarm2(initInfo ? user.getAlarm2() : null)
				.alarm3(initInfo ? user.getAlarm3() : null)
				.providerType(user.getProviderType())
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

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class CreateNicknameCommand {
		private final String nickname;
	}
}
