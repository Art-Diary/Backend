package klieme.artdiary.myexhs.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import klieme.artdiary.gatherings.ui.view.GatheringView;
import klieme.artdiary.myexhs.service.MyExhsOperationUseCase;
import klieme.artdiary.myexhs.service.MyExhsReadUseCase;
import klieme.artdiary.myexhs.ui.view.MyExhsView;

@RestController
@RequestMapping(value = "/myexhs")
public class MyExhsController {
	private final MyExhsOperationUseCase myExhsOperationUseCase;
	private final MyExhsReadUseCase myExhsReadUseCase;

	public MyExhsController(MyExhsOperationUseCase myExhsOperationUseCase, MyExhsReadUseCase myExhsReadUseCase) {
		this.myExhsOperationUseCase = myExhsOperationUseCase;
		this.myExhsReadUseCase = myExhsReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<List<MyExhsView>> getMyExhsList() {

		List<MyExhsReadUseCase.FindMyExhsResult> results = myExhsReadUseCase.getMyExhsList();

		List<MyExhsView> viewResult = new ArrayList<>();

		for (MyExhsReadUseCase.FindMyExhsResult result : results) {
			viewResult.add(MyExhsView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);

	}

}
