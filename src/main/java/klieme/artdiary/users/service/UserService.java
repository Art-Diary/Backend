package klieme.artdiary.users.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.ImageType;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.myexhs.data_access.entity.UserExhEntity;
import klieme.artdiary.myexhs.data_access.repository.UserExhRepository;
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
	private final RestTemplate restTemplate;

	@Autowired
	public UserService(UserRepository userRepository, UserExhRepository userExhRepository,
		GatheringDiaryRepository gatheringDiaryRepository, ReasonRepository reasonRepository,
		ImageTransfer imageTransfer, RestTemplate restTemplate) {
		this.userRepository = userRepository;
		this.userExhRepository = userExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.reasonRepository = reasonRepository;
		this.imageTransfer = imageTransfer;
		this.restTemplate = restTemplate;
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
	public void oauthCreate(OAuthCreateCommand command) {
		try {
			Map<String, Object> googleData = getGoogleData(command.getIdToken());
			System.out.println("google");
			System.out.println(googleData.get("email").toString());
			System.out.println(googleData.get("name").toString());
			System.out.println(googleData.get("picture").toString());

			// 있는지 확인
			UserEntity entity = UserEntity.builder()
				.email(googleData.get("email").toString())
				.nickname(googleData.get("name").toString())
				.profile(null)
				.providerType("google")
				.providerId("google")
				.favoriteArt(null)
				.alarm1(true)
				.alarm2(true)
				.alarm3(true)
				.build();
			userRepository.save(entity);
			ImageTransfer.FindUploadResult uploadResult = imageTransfer.uploadImage(ImageTransfer.UploadQuery.builder()
				.type(ImageType.PROFILE)
				.userId(entity.getUserId())
				.url(googleData.get("picture").toString())
				.build());
			entity.updateUser(UserEntity.builder().profile(uploadResult.getStoredPath()).build());
			userRepository.save(entity);
		} catch (Exception e) {
			System.out.println(e);
			throw new ArtDiaryException(MessageType.UNAUTHORIZED);
		}
	}

	private Map<String, Object> getGoogleData(String id_token) throws ParseException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);
		String googleApi = "https://oauth2.googleapis.com/tokeninfo";
		String targetUrl = UriComponentsBuilder.fromHttpUrl(googleApi)
			.queryParam("id_token", id_token)
			.build()
			.toUriString();

		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);

		JSONParser parser = new JSONParser();
		JSONObject jsonBody = (JSONObject)parser.parse(response.getBody());

		Map<String, Object> body = new ObjectMapper().readValue(jsonBody.toString(), Map.class);

		return body;
		// return OAuth2Attribute.of("google", "sub", body);
	}

	@Override
	@Transactional
	public FindUserResult updateUser(UserUpdateCommand command) throws IOException {
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
