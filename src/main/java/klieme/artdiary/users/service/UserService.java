package klieme.artdiary.users.service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.ImageType;
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

	@Override
	@Transactional
	public UserReadUseCase.FindUserResult updateUser(UserUpdateCommand command) throws IOException {
		UserEntity savedEntity = userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));
		// 닉네임 중복 확인
		if (!Objects.equals(savedEntity.getNickname(), command.getNickname())) {
			Optional<UserEntity> checkEntity = userRepository.findByNickname(command.getNickname());

			if (checkEntity.isPresent()) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			}
		}
		// 사용자 정보 업데이트
		ImageTransfer.FindUploadResult uploadResult = imageTransfer.uploadImage(ImageTransfer.UploadQuery.builder()
			.type(ImageType.PROFILE)
			.image(command.getProfile())
			.build());
		// 사용자 정보 업데이트
		savedEntity.updateUser(UserEntity.builder()
			.nickname(command.getNickname())
			.profile(uploadResult.getStoredPath() != null ? uploadResult.getStoredPath() : savedEntity.getProfile())
			.favoriteArt(command.getFavoriteArt())
			.alarm1(savedEntity.getAlarm1())
			.alarm2(savedEntity.getAlarm2())
			.alarm3(savedEntity.getAlarm3())
			.build());
		userRepository.save(savedEntity);
		return UserReadUseCase.FindUserResult.findUserInfo(savedEntity, uploadResult.getImageToString());
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
