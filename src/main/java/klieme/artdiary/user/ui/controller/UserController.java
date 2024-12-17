package klieme.artdiary.user.ui.controller;

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
import klieme.artdiary.user.service.UserOperationUseCase;
import klieme.artdiary.user.service.UserReadUseCase;
import klieme.artdiary.user.ui.request_body.AlarmTokenRequest;
import klieme.artdiary.user.ui.request_body.DeleteReasonRequest;
import klieme.artdiary.user.ui.request_body.TesterRequest;
import klieme.artdiary.user.ui.request_body.UpdateTokenRequest;
import klieme.artdiary.user.ui.request_body.UserAlarmRequest;
import klieme.artdiary.user.ui.request_body.UserNicknameRequest;
import klieme.artdiary.user.ui.request_body.UserRequest;
import klieme.artdiary.user.ui.request_body.UserUpdateRequest;
import klieme.artdiary.user.ui.view.AccessTokenView;
import klieme.artdiary.user.ui.view.UserAlarmView;
import klieme.artdiary.user.ui.view.UserNicknameView;
import klieme.artdiary.user.ui.view.UserView;
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
	public ResponseEntity<UserView> getUserInfo() {
		log.info("[사용자 정보 조회]");

		UserReadUseCase.FindUserResult result = userReadUseCase.getUserInfo();
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	@PostMapping("/verify")
	public ResponseEntity<UserNicknameView> verifyNickname(@Valid @RequestBody UserNicknameRequest request) {
		log.info("[닉네임 검사]");

		var command = UserReadUseCase.VerifyNicknameQuery.builder()
			.nickname(request.getNickname())
			.build();

		String result = userReadUseCase.verifyNickname(command);
		return ResponseEntity.ok(UserNicknameView.builder().nickname(result).build());
	}

	/**
	 * 사용자 로그인
	 * "/users"
	 */
	@PostMapping("")
	public ResponseEntity<UserView> loginUser(@Valid @RequestBody UserRequest userRequest) {
		log.info("[사용자 로그인 (" + userRequest.getProviderType() + ")]");

		var command = UserOperationUseCase.UserCreateCommand.builder()
			.email(userRequest.getEmail())
			.providerType(userRequest.getProviderType())
			.providerId(userRequest.getProviderId())
			.alarmToken(userRequest.getAlarmToken())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.socialLogin(true, false, command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	/**
	 * 테스터 사용자 로그인
	 * "/users/test"
	 */
	@PostMapping("/test")
	public ResponseEntity<UserView> loginUserTest(@Valid @RequestBody TesterRequest userRequest) {
		log.info("[테스터 사용자 로그인 (" + userRequest.getUserId() + ")]");

		UserReadUseCase.FindUserResult result = userOperationUseCase.loginTester(userRequest.getUserId());
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	/**
	 * 동일한 이메일 소셜 로그인 통합 진행
	 * "/users/unite"
	 */
	@PostMapping("/unite")
	public ResponseEntity<UserView> uniteSocialLogin(@Valid @RequestBody UserRequest userRequest) {
		log.info("[동일한 이메일 소셜 로그인 통합 진행 (" + userRequest.getProviderType() + ")]");

		var command = UserOperationUseCase.UserCreateCommand.builder()
			.email(userRequest.getEmail())
			.providerType(userRequest.getProviderType())
			.providerId(userRequest.getProviderId())
			.alarmToken(userRequest.getAlarmToken())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.socialLogin(false, true, command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	/**
	 * 동일한 이메일 소셜 로그인 분리
	 * "/users/separate"
	 */
	@PostMapping("/separate")
	public ResponseEntity<UserView> separateSocialLogin(@Valid @RequestBody UserRequest userRequest) {
		log.info("[동일한 이메일 소셜 로그인 분리 (" + userRequest.getProviderType() + ")]");

		var command = UserOperationUseCase.UserCreateCommand.builder()
			.email(userRequest.getEmail())
			.providerType(userRequest.getProviderType())
			.providerId(userRequest.getProviderId())
			.alarmToken(userRequest.getAlarmToken())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.socialLogin(false, false, command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}

	/**
	 * 사용자 프로필 설정
	 * "/users"
	 */
	@PatchMapping("")
	public ResponseEntity<UserView> updateUser(@Valid @ModelAttribute UserUpdateRequest request) {
		log.info("[사용자 프로필 설정]");
		var command = UserOperationUseCase.UserUpdateCommand.builder()
			.nickname(request.getNickname())
			.profile(request.getProfile())
			.favoriteArt(request.getFavoriteArt())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.updateUser(command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
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

	@PostMapping("/reissue")
	public ResponseEntity<AccessTokenView> reissueAccessToken(@Valid @RequestBody UpdateTokenRequest request) {
		log.info("[access token 재발급]");
		var command = UserReadUseCase.ReissueAccessTokenQuery.builder()
			.accessToken(request.getAccessToken())
			.build();

		UserReadUseCase.FindAccessTokenResult result = userReadUseCase.reissueAccessToken(command);
		return ResponseEntity.ok(AccessTokenView.builder().result(result).build());
	}

	/**
	 * 좋아요한 전시회 시작일/마감일 알림 설정 수정
	 * "/users/favorite-exh-alarm"
	 */
	@PatchMapping("/favorite-exh-alarm")
	public ResponseEntity<UserAlarmView> updateFavoriteExhAlarm(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[좋아요한 전시회 시작일/마감일 알림 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.favoriteExhAlarm(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	/**
	 * 혼자 가는 전시회 날짜 알림 설정 수정
	 * "/users/visit-solo-alarm"
	 */
	@PatchMapping("/visit-solo-alarm")
	public ResponseEntity<UserAlarmView> updateVisitSoloAlarm(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[혼자 가는 전시회 날짜 알림 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.visitSoloAlarm(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	/**
	 * 모임에서 가는 전시회 날짜 알림 설정 수정
	 * "/users/visit-gathering-alarm"
	 */
	@PatchMapping("/visit-gathering-alarm")
	public ResponseEntity<UserAlarmView> updateVisitGatheringAlarm(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[모임에서 가는 전시회 날짜 알림 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.visitGatheringAlarm(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	/**
	 * 새로운 모임 알림 설정 수정
	 * "/users/new-gathering-alarm"
	 */
	@PatchMapping("/new-gathering-alarm")
	public ResponseEntity<UserAlarmView> updateNewGatheringAlarm(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[새로운 모임 알림 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.newGatheringAlarm(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}

	/**
	 * 새로운 모임 알림 설정 수정
	 * "/users/new-date-gathering-alarm"
	 */
	@PatchMapping("/new-date-gathering-alarm")
	public ResponseEntity<UserAlarmView> updateNewDateGatheringAlarm(@Valid @RequestBody UserAlarmRequest request) {
		log.info("[새로운 모임 알림 설정]");

		var command = UserOperationUseCase.UserAlarmUpdateCommand.builder()
			.newDateGatheringAlarm(request.getAlarm())
			.build();
		UserReadUseCase.FindAlarmResult result = userOperationUseCase.updateAlarm(command);
		return ResponseEntity.ok(UserAlarmView.builder().result(result).build());
	}
}
