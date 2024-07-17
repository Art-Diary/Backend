package klieme.artdiary.user.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserOperationUseCase {
	UserReadUseCase.FindUserResult socialLogin(Boolean forCheckEmail, Boolean wantUnite,
		UserCreateCommand command) throws IOException;

	UserReadUseCase.FindUserResult updateUser(UserUpdateCommand command) throws IOException;

	UserReadUseCase.FindAlarmResult updateAlarm(UserAlarmUpdateCommand command);

	void deleteUser(DeleteReasonCommand command);

	void setAlarmToken(AlarmTokenUpdateCommand command);
	UserReadUseCase.FindUserResult loginTester(Long userId) throws IOException;

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class UserCreateCommand {
		private final String email;
		private final String providerType;
		private final String providerId;
		private final String alarmToken;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class UserUpdateCommand {
		private final String nickname;
		private final MultipartFile profile;
		private final String favoriteArt;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class UserAlarmUpdateCommand {
		private final Boolean alarm1;
		private final Boolean alarm2;
		private final Boolean alarm3;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class DeleteReasonCommand {
		private final String reason;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class AlarmTokenUpdateCommand {
		private final String alarmToken;
	}
}
