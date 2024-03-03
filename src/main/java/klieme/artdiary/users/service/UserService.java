package klieme.artdiary.users.service;

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
	public UserReadUseCase.FindUserResult updateUser(UserUpdateCommand command) {
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
			.profile(uploadResult.getStoredPath())
			.favoriteArt(command.getFavoriteArt())
			.build());
		userRepository.save(savedEntity);
		return UserReadUseCase.FindUserResult.findByUser(savedEntity, uploadResult.getImageToString());
	}

	@Override
	@Transactional
	public FindAlarmResult updateAlarm(UserAlarmUpdateCommand command) {
		UserEntity user = userRepository.findByUserId(getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		if (command.getAlarm1() != null) {
			if (user.getAlarm1().equals(command.getAlarm1())) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			} else {
				user.updateUser(UserEntity.builder()
					.alarm1(command.getAlarm1())
					.build());
			}
			return FindAlarmResult.findAlarm1(user);
		}
		if (command.getAlarm2() != null) {
			if (user.getAlarm2().equals(command.getAlarm2())) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			} else {
				user.updateUser(UserEntity.builder()
					.alarm2(command.getAlarm2())
					.build());
			}
			return FindAlarmResult.findAlarm2(user);
		}
		if (command.getAlarm3() != null) {
			if (user.getAlarm3().equals(command.getAlarm3())) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			} else {
				user.updateUser(UserEntity.builder()
					.alarm3(command.getAlarm3())
					.build());
			}
			return FindAlarmResult.findAlarm3(user);
		}
		return null;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
