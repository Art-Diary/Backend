package klieme.artdiary.solo.ui.controller;

import java.io.IOException;
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
import klieme.artdiary.solo.service.MyExhOperationUseCase;
import klieme.artdiary.solo.service.MyExhReadUseCase;
import klieme.artdiary.solo.ui.request_body.AddMyExhVisitDateRequest;
import klieme.artdiary.solo.ui.view.MyExhView;
import klieme.artdiary.solo.ui.view.MyStoredDateView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/myexhs")
public class MyExhController {
	private final MyExhOperationUseCase myExhOperationUseCase;
	private final MyExhReadUseCase myExhReadUseCase;

	@Autowired
	public MyExhController(MyExhOperationUseCase myExhOperationUseCase, MyExhReadUseCase myExhReadUseCase) {
		this.myExhOperationUseCase = myExhOperationUseCase;
		this.myExhReadUseCase = myExhReadUseCase;
	}

	/**
	 * 내 기록의 전시회 목록 조회
	 * "/myexhs"
	 */
	@GetMapping("")
	public ResponseEntity<List<MyExhView>> getMyExhsList() throws IOException {
		log.info("[내 기록의 전시회 목록 조회]");

		List<MyExhReadUseCase.FindMyExhsResult> results = myExhReadUseCase.getMyExhsList();

		List<MyExhView> viewResult = new ArrayList<>();

		for (MyExhReadUseCase.FindMyExhsResult result : results) {
			viewResult.add(MyExhView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);

	}

	/**
	 * 한 전시회에 대하여 캘린더에 저장된 날짜 조회
	 * /myexhs/:exhId
	 */
	@GetMapping("/{exhId}")
	public ResponseEntity<List<MyStoredDateView>> getStoredDateOfExhs(@PathVariable(name = "exhId") Long exhId) {
		log.info("[한 전시회에 대하여 캘린더에 저장된 날짜 조회]");
		var query = MyExhReadUseCase.MyStoredDateFindQuery.builder().exhId(exhId).build();
		// 비즈니스 로직 호출
		List<MyExhReadUseCase.FindMyStoredDateResult> results = myExhReadUseCase.getStoredDateOfExhs(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MyStoredDateView> viewResult = new ArrayList<>();
		long index = 0L;

		for (MyExhReadUseCase.FindMyStoredDateResult result : results) {
			viewResult.add(MyStoredDateView.builder().index(index++).result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	@PostMapping("") //혜원 추가
	public ResponseEntity<List<MyStoredDateView>> addSoloExhVisitDate(
		@Valid @RequestBody AddMyExhVisitDateRequest addMyExhVisitDateRequest) {

		log.info("[내 기록의 전시회 방문 날짜 추가]");

		var command = MyExhOperationUseCase.AddMyExhVisitDateCommand.builder()
			.visitDate(addMyExhVisitDateRequest.getVisitDate())
			.exhId(addMyExhVisitDateRequest.getExhId())
			.build();

		System.out.println(addMyExhVisitDateRequest.getVisitDate());

		List<MyExhReadUseCase.FindMyStoredDateResult> results = myExhOperationUseCase.addMyExhVisitDate(command);

		List<MyStoredDateView> viewResult = new ArrayList<>();

		for (MyExhReadUseCase.FindMyStoredDateResult result : results) {
			viewResult.add(MyStoredDateView.builder().result(result).build());
		}
		return ResponseEntity.created(null).body(viewResult);
	}
}
