package klieme.artdiary.users.ui.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	@GetMapping("/hello")
	public void helloPrint() {
		System.out.println("hello");
	}
}
