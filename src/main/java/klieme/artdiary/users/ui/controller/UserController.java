package klieme.artdiary.users.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.users.service.UserOperationUseCase;
import klieme.artdiary.users.ui.request_body.UserRequest;

@RestController
public class UserController {

	private final UserOperationUseCase userOperationUseCase;

	@Autowired
	public UserController(UserOperationUseCase userOperationUseCase) {
		this.userOperationUseCase = userOperationUseCase;
	}

	@GetMapping("/hello")
	public void helloPrint() {
		System.out.println("hello");
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
}
