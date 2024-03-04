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
import klieme.artdiary.gatherings.ui.view.GatheringView;
import klieme.artdiary.users.service.UserOperationUseCase;
import klieme.artdiary.users.service.UserReadUseCase;
import klieme.artdiary.users.ui.request_body.UserRequest;
import klieme.artdiary.users.ui.request_body.UserUpdateRequest;
import klieme.artdiary.users.ui.view.UserView;

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

	@GetMapping("/hello")
	public void helloPrint() {
		System.out.println("hello");
	}

	@GetMapping("")
	public ResponseEntity<UserView> getUserInfo() throws IOException {

		UserReadUseCase.FindUserResult result = userReadUseCase.getUserInfo();
		return ResponseEntity.created(null).body(UserView.builder().result(result).build());
	}

	@PostMapping("")
	public void createDummyData(@Valid @RequestBody UserRequest userRequest) {
		System.out.println("test");

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

	/**
	 * 사용자 프로필 설정
	 * "/users"
	 */
	@PatchMapping("")
	public ResponseEntity<UserView> updateUser(@Valid @ModelAttribute UserUpdateRequest request) throws IOException {
		var command = UserOperationUseCase.UserUpdateCommand.builder()
			.nickname(request.getNickname())
			.profile(request.getProfile())
			.favoriteArt(request.getFavoriteArt())
			.build();
		UserReadUseCase.FindUserResult result = userOperationUseCase.updateUser(command);
		return ResponseEntity.ok(UserView.builder().result(result).build());
	}
}
