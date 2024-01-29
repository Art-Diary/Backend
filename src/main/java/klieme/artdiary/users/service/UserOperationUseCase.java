package klieme.artdiary.users.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserOperationUseCase {

	String createDummy(UserDummyCreateCommand command);

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
}
