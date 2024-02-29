package klieme.artdiary.mate.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.mate.service.MateReadUseCase;
import klieme.artdiary.mate.ui.request_body.MateRequest;
import klieme.artdiary.mate.ui.view.MateView;

import klieme.artdiary.mate.service.MateOperationUseCase;
import klieme.artdiary.myexhs.service.MyExhsOperationUseCase;
import klieme.artdiary.myexhs.service.MyExhsReadUseCase;
import klieme.artdiary.myexhs.ui.view.MyStoredDateView;

@RestController
@RequestMapping(value = "/mates")
public class MateController {
	private final MateReadUseCase mateReadUseCase;
	private final MateOperationUseCase mateOperationUseCase;

	@Autowired
	public MateController(MateReadUseCase mateReadUseCase, MateOperationUseCase mateOperationUseCase) {
		this.mateReadUseCase = mateReadUseCase;
		this.mateOperationUseCase = mateOperationUseCase;
	}

	@GetMapping("")
	public ResponseEntity<List<MateView>> getMateList() {
		// 비즈니스 로직 호출
		List<MateReadUseCase.FindMateResult> mateList = mateReadUseCase.getMateList();
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MateView> results = new ArrayList<>();

		for (MateReadUseCase.FindMateResult mate : mateList) {
			results.add(MateView.builder().result(mate).build());
		}
		return ResponseEntity.ok(results);
	}

	@PostMapping("")
	public ResponseEntity<List<MateView>> addNewMate(@Valid @RequestBody MateRequest mateRequest) {

		var command = MateOperationUseCase.AddMyMateCreateDummy.builder()
			.toUserId(mateRequest.getUserId())
			.build();

		List<MateReadUseCase.FindMateResult> results = mateOperationUseCase.addMyMateCreate(command);

		List<MateView> viewResult = new ArrayList<>();

		for (MateReadUseCase.FindMateResult result : results) {
			viewResult.add(MateView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	@GetMapping("/search")
	public ResponseEntity<List<MateView>> searchNewMate(
		@RequestParam(name = "nickname", required = false) String nickname) {

		List<MateView> results = new ArrayList<>();

		List<MateReadUseCase.FindMateResult> mateList = mateReadUseCase.searchNewMate(nickname);
		for (MateReadUseCase.FindMateResult mate : mateList) {
			results.add(MateView.builder().result(mate).build());
		}
		return ResponseEntity.ok(results);
	}

}
