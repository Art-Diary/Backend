package klieme.artdiary.users.ui.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.users.service.UserOperationUseCase;
import klieme.artdiary.users.service.UserReadUseCase;
import klieme.artdiary.users.ui.request_body.DeleteReasonRequest;
import klieme.artdiary.users.ui.request_body.OAuthGoogleRequest;
import klieme.artdiary.users.ui.request_body.UserAlarmRequest;
import klieme.artdiary.users.ui.request_body.UserNicknameRequest;
import klieme.artdiary.users.ui.request_body.UserRequest;
import klieme.artdiary.users.ui.request_body.UserUpdateRequest;
import klieme.artdiary.users.ui.view.UserAlarmView;
import klieme.artdiary.users.ui.view.UserNicknameView;
import klieme.artdiary.users.ui.view.UserView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

	private final UserOperationUseCase userOperationUseCase;
	private final UserReadUseCase userReadUseCase;

	@Autowired
	public UserController(UserOperationUseCase userOperationUseCase, UserReadUseCase userReadUseCase) {
		this.userOperationUseCase = userOperationUseCase;
		this.userReadUseCase = userReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<UserView> getUserInfo() throws IOException {
		log.info("[사용자 정보 조회]");

		UserReadUseCase.FindUserResult result = userReadUseCase.getUserInfo();
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	@PostMapping("/verify")
	public ResponseEntity<UserNicknameView> verifyNickname(@Valid @RequestBody UserNicknameRequest request) {
		log.info("[닉네임 검사]");

		var command = UserReadUseCase.CreateNicknameCommand.builder()
			.nickname(request.getNickname())
			.build();

		String result = userReadUseCase.verifyNickname(command);
		return ResponseEntity.ok(UserNicknameView.builder().nickname(result).build());
	}

	@PostMapping("")
	public void createDummyData(@Valid @RequestBody UserRequest userRequest) {
		log.info("[새로운 사용자 추가]");

		var command = UserOperationUseCase.UserDummyCreateCommand.builder()
			.email(userRequest.getEmail())
			.nickname(userRequest.getNickname())
			.profile(userRequest.getProfile())
			.providerType(userRequest.getProviderType())
			.providerId(userRequest.getProviderId())
			.favoriteArt(userRequest.getFavoriteArt())
			.alarm1(userRequest.getAlarm1())
			.alarm2(userRequest.getAlarm2())
			.alarm3(userRequest.getAlarm3())
			.build();
		System.out.println(userOperationUseCase.createDummy(command));
	}

	@PostMapping("/oauth/google")
	public ResponseEntity<UserView> oauthGoogle(@Valid @RequestBody OAuthGoogleRequest request) {
		log.info("[새로운 사용자 추가 (Google)]");

		var command = UserOperationUseCase.OAuthCreateCommand.builder()
			.idToken(request.getIdToken())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.oauthCreate(command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	/**
	 * 사용자 프로필 설정
	 * "/users"
	 */
	@PatchMapping("")
	public ResponseEntity<UserView> updateUser(@Valid @ModelAttribute UserUpdateRequest request) throws IOException {
		log.info("[사용자 프로필 설정]");
		var command = UserOperationUseCase.UserUpdateCommand.builder()
			.nickname(request.getNickname())
			.profile(request.getProfile())
			.favoriteArt(request.getFavoriteArt())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.updateUser(command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	/**
	 * 알림1 설정 수정
	 * "/users/alarm1"
	 */
	@PatchMapping("/alarm1")
	public ResponseEntity<UserAlarmView> updateAlarm1(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[알림1 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.alarm1(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	/**
	 * 알림1 설정 수정
	 * "/users/alarm2"
	 */
	@PatchMapping("/alarm2")
	public ResponseEntity<UserAlarmView> updateAlarm2(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[알림2 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.alarm2(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	/**
	 * 알림1 설정 수정
	 * "/users/alarm3"
	 */
	@PatchMapping("/alarm3")
	public ResponseEntity<UserAlarmView> updateAlarm3(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[알림3 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.alarm3(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	@PostMapping("/leave")
	public void deleteUser(@Valid @RequestBody DeleteReasonRequest request) {
		log.info("[사용자 삭제]");
		var command = UserOperationUseCase.DeleteReasonCommand.builder()
			.reason(request.getReason())
			.build();

		userOperationUseCase.deleteUser(command);

	}
}
