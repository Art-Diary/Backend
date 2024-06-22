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
import klieme.artdiary.users.ui.request_body.AlarmTokenRequest;
import klieme.artdiary.users.ui.request_body.DeleteReasonRequest;
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
	public ResponseEntity<UserView> loginUser(@Valid @RequestBody UserRequest userRequest) throws IOException {
		log.info("[새로운 사용자 추가 (" + userRequest.getProviderType() + ")]");

		var command = UserOperationUseCase.UserCreateCommand.builder()
			.email(userRequest.getEmail())
			.providerType(userRequest.getProviderType())
			.providerId(userRequest.getProviderId())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.loginUser(command);
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

	@PatchMapping("/alarm-token")
	public void setAlarmToken(@Valid @RequestBody AlarmTokenRequest request) {
		log.info("[사용자 푸시 알림 토큰]");
		var command = UserOperationUseCase.AlarmTokenUpdateCommand.builder()
			.alarmToken(request.getAlarmToken())
			.build();

		userOperationUseCase.setAlarmToken(command);

	}
}
