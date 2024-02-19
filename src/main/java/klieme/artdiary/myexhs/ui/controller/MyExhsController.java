package klieme.artdiary.myexhs.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.exhibitions.service.ExhOperationUseCase;
import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import klieme.artdiary.exhibitions.ui.request_body.AddSoloExhRequest;
import klieme.artdiary.exhibitions.ui.view.StoredDateView;
import klieme.artdiary.myexhs.service.MyExhsOperationUseCase;
import klieme.artdiary.myexhs.service.MyExhsReadUseCase;
import klieme.artdiary.myexhs.ui.request_body.AddMyExhsVisitDateRequest;
import klieme.artdiary.myexhs.ui.view.MyExhsView;
import klieme.artdiary.myexhs.ui.view.MyStoredDateView;

@RestController
@RequestMapping(value = "/myexhs")
public class MyExhsController {
	private final MyExhsOperationUseCase myExhsOperationUseCase;
	private final MyExhsReadUseCase myExhsReadUseCase;

	@Autowired
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

	@PostMapping("") //혜원 추가
	public ResponseEntity<List<MyStoredDateView>> addSoloExhVisitDate(
		@Valid @RequestBody AddMyExhsVisitDateRequest addMyExhsVisitDateRequest) {

		System.out.println("Test");

		var command = MyExhsOperationUseCase.AddMyExhVisitDateDummyCommand.builder()
			.visitDate(addMyExhsVisitDateRequest.getVisitDate())
			.exhId(addMyExhsVisitDateRequest.getExhId())
			.build();

		List<MyExhsReadUseCase.FindMyStoredDateResult> results = myExhsOperationUseCase.addMyExhVisitDateDummy(command);

		List<MyStoredDateView> viewResult = new ArrayList<>();

		for (MyExhsReadUseCase.FindMyStoredDateResult result : results) {
			viewResult.add(MyStoredDateView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
		//	System.out.println(MyExhsOperationUseCase.(command));
		//return ResponseEntity.created(null).body(MyStoredDateView.builder().result(results).build());
	}

	@GetMapping("/{exhId}")
	public ResponseEntity<List<MyStoredDateView>> getStoredDateOfExhs(@PathVariable(name = "exhId") Long exhId) {
		var query = MyExhsReadUseCase.MyStoredDateFindQuery.builder().exhId(exhId).build();
		// 비즈니스 로직 호출
		List<MyExhsReadUseCase.FindMyStoredDateResult> results = myExhsReadUseCase.getStoredDateOfExhs(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MyStoredDateView> viewResult = new ArrayList<>();

		for (MyExhsReadUseCase.FindMyStoredDateResult result : results) {
			viewResult.add(MyStoredDateView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}
}
