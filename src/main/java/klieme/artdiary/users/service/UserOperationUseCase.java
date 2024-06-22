package klieme.artdiary.users.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserOperationUseCase {

	UserReadUseCase.FindUserResult loginUser(UserCreateCommand command) throws IOException;

	UserReadUseCase.FindUserResult updateUser(UserUpdateCommand command) throws IOException;

	UserReadUseCase.FindAlarmResult updateAlarm(UserAlarmUpdateCommand command);

	void deleteUser(DeleteReasonCommand command);

	void setAlarmToken(AlarmTokenUpdateCommand command);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class UserCreateCommand {
		private final String email;
		private final String providerType;
		private final String providerId;
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
