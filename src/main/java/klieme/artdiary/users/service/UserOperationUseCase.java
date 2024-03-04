package klieme.artdiary.users.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserOperationUseCase {

	String createDummy(UserDummyCreateCommand command);

	UserReadUseCase.FindUserResult updateUser(UserUpdateCommand command) throws IOException;

	UserReadUseCase.FindAlarmResult updateAlarm(UserAlarmUpdateCommand command);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class UserDummyCreateCommand {
		private final String email;
		private final String nickname;
		private final String profile;
		private final String providerType;
		private final String providerId;
		private final String favoriteArt;
		private final Boolean alarm1;
		private final Boolean alarm2;
		private final Boolean alarm3;
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
}
