package klieme.artdiary.user.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.image.ImageType;
import klieme.artdiary.common.image.S3ImageTransfer;
import klieme.artdiary.common.jwt.JwtUtil;
import klieme.artdiary.common.jwt.TokenInfo;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.ReasonEntity;
import klieme.artdiary.user.data_access.entity.SocialLoginEntity;
import klieme.artdiary.user.data_access.entity.SocialLoginId;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.ReasonRepository;
import klieme.artdiary.user.data_access.repository.SocialLoginRepository;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {

	private final UserRepository userRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final ReasonRepository reasonRepository;
	private final SocialLoginRepository socialLoginRepository;
	private final JwtUtil jwtUtil;
	private final S3ImageTransfer s3ImageTransfer;

	@Autowired
	public UserService(UserRepository userRepository, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository, ReasonRepository reasonRepository, SocialLoginRepository socialLoginRepository,
		JwtUtil jwtUtil, S3ImageTransfer s3ImageTransfer) {
		this.userRepository = userRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.reasonRepository = reasonRepository;
		this.socialLoginRepository = socialLoginRepository;
		this.jwtUtil = jwtUtil;
		this.s3ImageTransfer = s3ImageTransfer;
	}

	@Override
	public FindUserResult getUserInfo() {
		UserEntity user = userRepository.findByUserId(getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		return FindUserResult.findUserInfo(user);
	}

	@Override
	public String verifyNickname(VerifyNicknameQuery command) {

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
	public FindUserResult loginTester(Long userId) {
		UserEntity userEntity = userRepository.findByUserId(userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		TokenInfo tokenInfo = jwtUtil.generateToken(userEntity.getUserId(), null);

		userEntity.updateRefreshToken(tokenInfo.getRefreshToken());
		return FindUserResult.findUserLoginInfo(userEntity, true, tokenInfo.getAccessToken());
	}

	@Transactional
	@Override
	public FindUserResult socialLogin(Boolean forCheckEmail, Boolean wantUnite, UserCreateCommand command) {
		// 재로그인 확인
		Optional<SocialLoginEntity> socialLoginEntity = socialLoginRepository.findBySocialLoginId(
			SocialLoginId.builder()
				.providerType(command.getProviderType())
				.providerUserId(command.getProviderId()).build());
		UserEntity userEntity;

		if (socialLoginEntity.isPresent()) {
			// re
			// update provider type
			userEntity = userRepository.findByUserId(socialLoginEntity.get().getUserId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			userEntity.updateUser(UserEntity.builder()
				.providerType(command.getProviderType())
				.alarmToken(command.getAlarmToken())
				.build());
			userRepository.save(userEntity);
		} else {
			// init
			userEntity = initLogin(forCheckEmail, wantUnite, command);
			insertSocialLogin(command, userEntity);
		}
		TokenInfo tokenInfo = jwtUtil.generateToken(userEntity.getUserId(), null);
		userEntity.updateRefreshToken(tokenInfo.getRefreshToken());

		Boolean finishInit = !Objects.equals(userEntity.getNickname(),
			command.getProviderType() + "_" + command.getProviderId());

		return FindUserResult.findUserLoginInfo(userEntity, finishInit, tokenInfo.getAccessToken());
	}

	private UserEntity initLogin(Boolean forCheckEmail, Boolean wantUnite, UserCreateCommand command) {
		// 초기 로그인
		if (forCheckEmail) {
			Boolean isExistedEmail = userRepository.existsByEmail(command.getEmail());

			if (isExistedEmail) {
				// 409 에러
				throw new ArtDiaryException(MessageType.CONFLICT);
			}
		}

		UserEntity userEntity;

		if (wantUnite) {
			userEntity = userRepository.findByEmail(command.getEmail())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		} else {
			// 새로운 계정 생성
			userEntity = insertUser(command);
		}
		return userEntity;
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
		String uploadImageUrl = s3ImageTransfer.uploadImageToStorage(
			S3ImageTransfer.UploadQuery.builder()
				.type(ImageType.PROFILE)
				.image(command.getProfile())
				.prevImagePath(savedEntity.getProfile())
				.build());
		// 사용자 정보 업데이트
		savedEntity.updateUser(UserEntity.builder()
			.nickname(command.getNickname())
			.profile(uploadImageUrl)
			.favoriteArt(command.getFavoriteArt())
			.build());
		userRepository.save(savedEntity);
		return FindUserResult.findUserInfo(savedEntity);
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
		// - ExhVisit의 writerId와 Diary의 userId 값을 null로 변경
		List<ExhVisitEntity> exhVisitList = exhVisitRepository.findByUserId(getUserId());
		List<DiaryEntity> diaryList = diaryRepository.findByWriterId(getUserId());

		for (ExhVisitEntity exhVisit : exhVisitList) {
			exhVisit.updateUserIdNull();
			exhVisitRepository.save(exhVisit);
		}
		for (DiaryEntity diary : diaryList) {
			diary.updateWriterIdNull();
			diaryRepository.save(diary);
		}

		// - 탈퇴 이유 reason에 저장.
		ReasonEntity reason = ReasonEntity.builder().reason(command.getReason()).build();
		reasonRepository.save(reason);

		// - user 테이블에서 사용자 삭제
		userRepository.deleteById(getUserId());
	}

	@Override
	@Transactional
	public void setAlarmToken(AlarmTokenUpdateCommand command) {
		UserEntity savedEntity = userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));

		savedEntity.updateUser(UserEntity.builder().alarmToken(command.getAlarmToken()).build());
	}

	@Override
	public FindAccessTokenResult reissueAccessToken(ReissueAccessTokenQuery command) {
		// access token 검증
		if (!jwtUtil.validateToken(command.getAccessToken(), true)) {
			throw new ArtDiaryException(MessageType.ReLogin);
		}
		// refresh token 검증
		Long userId = jwtUtil.getUserId(command.getAccessToken());
		UserEntity userEntity = userRepository.findByUserId(userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.ReLogin));

		if (!jwtUtil.validateToken(userEntity.getRefreshToken(), false)) {
			throw new ArtDiaryException(MessageType.ReLogin);
		}
		// access token 발급
		TokenInfo tokenInfo = jwtUtil.generateToken(userEntity.getUserId(), true);

		return FindAccessTokenResult.findAccessToken(tokenInfo.getAccessToken());
	}

	private Long getUserId() {
		return getCurrentUserId();
	}

	private UserEntity insertUser(UserCreateCommand command) {
		UserEntity newUser = UserEntity.builder()
			.email(command.getEmail())
			.nickname(command.getProviderType() + "_" + command.getProviderId())
			.profile(null)
			.favoriteArt(null)
			.alarm1(true)
			.alarm2(true)
			.alarm3(true)
			.alarmToken(command.getAlarmToken())
			.providerType(command.getProviderType())
			.build();
		userRepository.save(newUser);
		return newUser;
	}

	private void insertSocialLogin(UserCreateCommand command, UserEntity user) {
		SocialLoginEntity newSocialLogin = SocialLoginEntity.builder()
			.socialLoginId(SocialLoginId.builder()
				.providerType(command.getProviderType())
				.providerUserId(command.getProviderId())
				.build())
			.userId(user.getUserId())
			.build();
		socialLoginRepository.save(newSocialLogin);
	}
}
