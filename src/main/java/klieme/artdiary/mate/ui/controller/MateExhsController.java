package klieme.artdiary.mate.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.mate.service.MateExhReadUseCase;
import klieme.artdiary.mate.ui.view.MateDiaryView;
import klieme.artdiary.mate.ui.view.MateExhsView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/mates/{mateId}/exhibitions")
public class MateExhsController {
	private final MateExhReadUseCase mateExhReadUseCase;

	@Autowired
	public MateExhsController(MateExhReadUseCase mateExhReadUseCase) {
		this.mateExhReadUseCase = mateExhReadUseCase;
	}

	/**
	 * 전시 메이트가 갔다온 전시회 목록
	 * "/mates/:mateId/exhibitions"
	 */
	@GetMapping("")
	public ResponseEntity<List<MateExhsView>> getMateExhsList(@PathVariable(name = "mateId") Long mateId) {
		log.info("[전시 메이트가 갔다온 전시회 목록]");
		var query = MateExhReadUseCase.MateExhsFindQuery.builder().mateId(mateId).build();
		// 비즈니스 로직 호출
		List<MateExhReadUseCase.FindMateExhsResult> results = mateExhReadUseCase.getMateExhsList(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MateExhsView> viewResult = new ArrayList<>();

		for (MateExhReadUseCase.FindMateExhsResult result : results) {
			viewResult.add(MateExhsView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	/**
	 * 전시 메이트의 전시회 기록 목록
	 * "/mates/:mateId/exhibitions/:exhId/diaries"
	 */
	@GetMapping("/{exhId}/diaries")
	public ResponseEntity<List<MateDiaryView>> getMateDiaries(@PathVariable(name = "mateId") Long mateId,
		@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시 메이트의 전시회 기록 목록]");
		var query = MateExhReadUseCase.MateDiaryFindQuery.builder().mateId(mateId).exhId(exhId).build();

		//비즈니스 로직
		List<MateExhReadUseCase.FindMateDiaryResult> results = mateExhReadUseCase.getMateDiaryList(query);

		List<MateDiaryView> viewResult = new ArrayList<>();

		for (MateExhReadUseCase.FindMateDiaryResult result : results) {
			viewResult.add(MateDiaryView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}
}
