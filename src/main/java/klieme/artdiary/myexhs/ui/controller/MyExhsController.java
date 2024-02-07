package klieme.artdiary.myexhs.ui.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.myexhs.service.MyExhsOperationUseCase;
import klieme.artdiary.myexhs.service.MyExhsReadUseCase;

@RestController
@RequestMapping(value = "/myexhs")
public class MyExhsController {
	private final MyExhsOperationUseCase myExhsOperationUseCase;
	private final MyExhsReadUseCase myExhsReadUseCase;

	public MyExhsController(MyExhsOperationUseCase myExhsOperationUseCase, MyExhsReadUseCase myExhsReadUseCase) {
		this.myExhsOperationUseCase = myExhsOperationUseCase;
		this.myExhsReadUseCase = myExhsReadUseCase;
	}
}
