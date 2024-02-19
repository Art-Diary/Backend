package klieme.artdiary.mate.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.mate.service.MateReadUseCase;
import klieme.artdiary.mate.ui.view.MateView;

@RestController
@RequestMapping(value = "/mates")
public class MateController {
	private final MateReadUseCase mateReadUseCase;

	@Autowired
	public MateController(MateReadUseCase mateReadUseCase) {
		this.mateReadUseCase = mateReadUseCase;
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
}
