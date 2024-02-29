package klieme.artdiary.mate.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.mate.service.MateOperationUseCase;
import klieme.artdiary.mate.service.MateReadUseCase;
import klieme.artdiary.mate.ui.request_body.MateRequest;
import klieme.artdiary.mate.ui.view.MateView;

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

	/**
	 * 전시 메이트 목록 조회
	 * "/mates"
	 */
	@GetMapping("")
	public ResponseEntity<List<MateView>> getMateList() throws IOException {
		// 비즈니스 로직 호출
		List<MateReadUseCase.FindMateResult> mateList = mateReadUseCase.getMateList();
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MateView> results = new ArrayList<>();

		for (MateReadUseCase.FindMateResult mate : mateList) {
			results.add(MateView.builder().result(mate).build());
		}
		return ResponseEntity.ok(results);
	}

	/**
	 * 전시 메이트 추가
	 * "/mates"
	 */
	@PostMapping("")
	public ResponseEntity<List<MateView>> addNewMate(@Valid @RequestBody MateRequest mateRequest) throws IOException {

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

	/**
	 * 전시 메이트 추가할 때 닉네임 검색
	 * "/mates/search?nickname=[]"
	 */
	@GetMapping("/search")
	public ResponseEntity<List<MateView>> searchNewMate(
		@RequestParam(name = "nickname", required = false) String nickname) throws IOException {

		List<MateView> results = new ArrayList<>();

		List<MateReadUseCase.FindMateResult> mateList = mateReadUseCase.searchNewMate(nickname);
		for (MateReadUseCase.FindMateResult mate : mateList) {
			results.add(MateView.builder().result(mate).build());
		}
		return ResponseEntity.ok(results);
	}

}
