package klieme.artdiary.mydiarys.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.mydiarys.service.MydiaryOperationUseCase;
import klieme.artdiary.mydiarys.service.MydiaryReadUseCase;

@RestController
@RequestMapping(value = "/myexhs/{exhId}/diaries")
public class MydiaryController {
	private final MydiaryOperationUseCase mydiaryOperationUseCase;
	private final MydiaryReadUseCase mydiaryReadUseCase;

	@Autowired
	public MydiaryController(MydiaryOperationUseCase mydiaryOperationUseCase, MydiaryReadUseCase mydiaryReadUseCase) {
		this.mydiaryOperationUseCase = mydiaryOperationUseCase;
		this.mydiaryReadUseCase = mydiaryReadUseCase;
	}
}
