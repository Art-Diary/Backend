package klieme.artdiary.users.service;

import java.io.IOException;
import java.util.List;
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
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.users.data_access.entity.ReasonEntity;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.ReasonRepository;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {

	private final UserRepository userRepository;
	private final UserExhRepository userExhRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final ReasonRepository reasonRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public UserService(UserRepository userRepository, UserExhRepository userExhRepository,
		GatheringDiaryRepository gatheringDiaryRepository, ReasonRepository reasonRepository,
		ImageTransfer imageTransfer) {
		this.userRepository = userRepository;
		this.userExhRepository = userExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.reasonRepository = reasonRepository;
		this.imageTransfer = imageTransfer;
	}

	@Override
	public FindUserResult getUserInfo() throws IOException {
		UserEntity user = userRepository.findByUserId(getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		String profile = imageTransfer.downloadImage(user.getProfile());
		FindUserResult result = FindUserResult.findUserInfo(user, profile);
		return result;
	}

	@Override
	public String verifyNickname(CreateNicknameCommand command) {

		//기존 닉네임 가져오기 => contain 사용해서 바로 닉네임 찾는 쿼리 사용해도 될 것 같음. (by 채린)
		List<UserEntity> userNicknameList = userRepository.findAll();

		//닉네임 한글만!
		//닉네임 확인 & 있는 경우 에러 발생.
		for (UserEntity user : userNicknameList) {
			if (Objects.equals(command.getNickname(), user.getNickname())) {
				throw (new ArtDiaryException(MessageType.CONFLICT));
			}
		}
		//없으면 저장하기?

		return command.getNickname();
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
	public FindUserResult updateUser(UserUpdateCommand command) {
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
		return FindUserResult.findUserInfo(savedEntity, uploadResult.getImageToString());
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

	@Override
	@Transactional
	public void deleteUser(DeleteReasonCommand command) {

		//- UserExh, GatheringDiary 의 탈퇴 userId Null 로 변경.
		// userExh에서 확인
		List<UserExhEntity> userExhs = userExhRepository.findByUserId(getUserId());
		for (UserExhEntity userExh : userExhs) {
			userExh.updateUserId();
			userExhRepository.save(userExh);
		}
		//GatheringDiary에서 확인
		List<GatheringDiaryEntity> gDiaries = gatheringDiaryRepository.findByUserId(getUserId());
		for (GatheringDiaryEntity gDiary : gDiaries) {
			gDiary.updateUserId();
			gatheringDiaryRepository.save(gDiary);
		}

		// - 탈퇴 이유 reason에 저장.
		ReasonEntity reason = ReasonEntity.builder()
			.reason(command.getReason()).build();
		reasonRepository.save(reason);

		// - user테이블에서 사용자 삭제
		userRepository.deleteById(getUserId());

	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
