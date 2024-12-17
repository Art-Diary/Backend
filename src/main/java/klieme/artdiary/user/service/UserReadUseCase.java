package klieme.artdiary.user.service;

import java.util.Objects;

import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserReadUseCase {

	FindUserResult getUserInfo();

	String verifyNickname(VerifyNicknameQuery command);

	FindAccessTokenResult reissueAccessToken(ReissueAccessTokenQuery command);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class VerifyNicknameQuery {
		private final String nickname;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class ReissueAccessTokenQuery {
		private final String accessToken;
	}

	@Getter
	@ToString
	@Builder
	class FindUserResult {
		private final Long userId;
		private final String nickname;
		private final String email;
		private final String profile;
		private final String favoriteArt;
		private final Boolean favoriteExhAlarm;
		private final Boolean visitSoloAlarm;
		private final Boolean visitGatheringAlarm;
		private final Boolean newGatheringAlarm;
		private final Boolean newDateGatheringAlarm;
		private final Boolean initInfo;
		private final String providerType;
		private final String accessToken;
		private final String roleType;

		public static FindUserResult findUserInfo(UserEntity user) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.email(user.getEmail())
				.profile(user.getProfile())
				.favoriteArt(user.getFavoriteArt() == null || Objects.equals(user.getFavoriteArt(), ".") ? "그외" :
					user.getFavoriteArt())
				.favoriteExhAlarm(user.getFavoriteExhAlarm())
				.visitSoloAlarm(user.getVisitSoloAlarm())
				.visitGatheringAlarm(user.getVisitGatheringAlarm())
				.newGatheringAlarm(user.getNewGatheringAlarm())
				.newDateGatheringAlarm(user.getNewDateGatheringAlarm())
				.providerType(user.getProviderType())
				.roleType(user.getRoleType())
				.build();
		}

		public static FindUserResult findUserLoginInfo(UserEntity user, Boolean initInfo, String accessToken) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.email(user.getEmail())
				.initInfo(initInfo)
				.nickname(initInfo ? user.getNickname() : null)
				.profile(initInfo ? user.getProfile() : null)
				.favoriteArt(
					initInfo ? (user.getFavoriteArt() == null || Objects.equals(user.getFavoriteArt(), ".") ? "그외" :
						user.getFavoriteArt()) : null)
				.favoriteExhAlarm(initInfo ? user.getFavoriteExhAlarm() : null)
				.visitSoloAlarm(initInfo ? user.getVisitSoloAlarm() : null)
				.visitGatheringAlarm(initInfo ? user.getVisitGatheringAlarm() : null)
				.newGatheringAlarm(initInfo ? user.getNewGatheringAlarm() : null)
				.newDateGatheringAlarm(initInfo ? user.getNewDateGatheringAlarm() : null)
				.providerType(user.getProviderType())
				.accessToken(accessToken)
				.roleType(user.getRoleType())
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindAlarmResult {
		private final Boolean alarm;

		@Builder
		public static FindAlarmResult findAlarm(Boolean alarm) {
			return FindAlarmResult.builder()
				.alarm(alarm)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindAccessTokenResult {
		private final String accessToken;

		@Builder
		public static FindAccessTokenResult findAccessToken(String accessToken) {
			return FindAccessTokenResult.builder()
				.accessToken(accessToken)
				.build();
		}
	}
}
