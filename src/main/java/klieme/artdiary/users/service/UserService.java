package klieme.artdiary.users.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {

	private final UserRepository userRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public UserService(UserRepository userRepository, ImageTransfer imageTransfer) {
		this.userRepository = userRepository;
		this.imageTransfer = imageTransfer;
	}

	@Override
	public UserReadUseCase.FindUserResult getUserInfo() throws IOException {
		UserEntity user = userRepository.findByUserId(getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		String profile = imageTransfer.downloadImage(user.getProfile());
		UserReadUseCase.FindUserResult result = UserReadUseCase.FindUserResult.findUserInfo(user, profile);
		return result;
	}

	@Transactional
	@Override
	public String createDummy(UserDummyCreateCommand command) {
		UserEntity entity = UserEntity.builder()
			.email(command.getEmail())
			.nickname(command.getNickname())
			.profile(command.getProfile())
			.providerType(command.getProviderType())
			.providerId(command.getProviderId())
			.favoriteArt(command.getFavoriteArt())
			.alarm1(command.getAlarm1())
			.alarm2(command.getAlarm2())
			.alarm3(command.getAlarm3())
			.build();
		userRepository.save(entity);
		return "complete";
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
