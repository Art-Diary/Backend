package klieme.artdiary.mateexhs.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.mateexhs.service.MateExhsReadUseCase;
import klieme.artdiary.mateexhs.ui.view.MateDiaryView;
import klieme.artdiary.mateexhs.ui.view.MateExhsView;

@RestController
@RequestMapping(value = "/mates/{mateId}/exhibitions")
public class MateExhsController {
	private final MateExhsReadUseCase mateExhsReadUseCase;

	@Autowired
	public MateExhsController(MateExhsReadUseCase mateExhsReadUseCase) {
		this.mateExhsReadUseCase = mateExhsReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<List<MateExhsView>> getMateExhsList(@PathVariable(name = "mateId") Long mateId) {
		var query = MateExhsReadUseCase.MateExhsFindQuery.builder().mateId(mateId).build();
		// 비즈니스 로직 호출
		List<MateExhsReadUseCase.FindMateExhsResult> results = mateExhsReadUseCase.getMateExhsList(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MateExhsView> viewResult = new ArrayList<>();

		for (MateExhsReadUseCase.FindMateExhsResult result : results) {
			viewResult.add(MateExhsView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	@GetMapping("/{exhId}/diaries")
	public ResponseEntity<List<MateDiaryView>> getMateDiaries(@PathVariable(name = "mateId") Long mateId,
		@PathVariable(name = "exhId") Long exhId) {
		var query = MateExhsReadUseCase.MateDiaryFindQuery.builder().mateId(mateId).exhId(exhId).build();

		//비즈니스 로직
		List<MateExhsReadUseCase.FindMateDiaryResult> results = mateExhsReadUseCase.getMateDiaryList(query);

		List<MateDiaryView> viewResult = new ArrayList<>();

		for (MateExhsReadUseCase.FindMateDiaryResult result : results) {
			viewResult.add(MateDiaryView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}
}
