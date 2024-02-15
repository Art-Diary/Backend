package klieme.artdiary.gatherings.ui.controller;

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
import klieme.artdiary.gatherings.service.GatheringOperationUseCase;
import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import klieme.artdiary.gatherings.ui.request_body.AddExhDateRequest;
import klieme.artdiary.gatherings.ui.request_body.AddGatheringRequest;
import klieme.artdiary.gatherings.ui.view.GatheringExhView;
import klieme.artdiary.gatherings.ui.view.GatheringView;

@RestController
@RequestMapping(value = "/gatherings")
public class GatheringController {
	private final GatheringOperationUseCase gatheringOperationUseCase;
	private final GatheringReadUseCase gatheringReadUseCase;

	@Autowired
	public GatheringController(GatheringOperationUseCase gatheringOperationUseCase,
		GatheringReadUseCase gatheringReadUseCase) {
		this.gatheringOperationUseCase = gatheringOperationUseCase;
		this.gatheringReadUseCase = gatheringReadUseCase;
	}

	@PostMapping("")
	public ResponseEntity<GatheringView> createGathering(@Valid @RequestBody AddGatheringRequest request) {
		// request body 데이터 받아오기
		var command = GatheringOperationUseCase.GatheringCreateCommand.builder()
			.gatherName(request.getGatherName())
			.build();
		// 비즈니스 로직 호출
		GatheringReadUseCase.FindGatheringResult result = gatheringOperationUseCase.createGathering(command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 반환
		return ResponseEntity.created(null).body(GatheringView.builder().result(result).build());
	}

	@GetMapping("")
	public ResponseEntity<List<GatheringView>> getGatheringList() {

		List<GatheringReadUseCase.FindGatheringResult> results = gatheringReadUseCase.getGatheringList();

		List<GatheringView> viewResult = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringResult result : results) {
			viewResult.add(GatheringView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);

	}

	/**
	 * 모임의 일정에 전시회 관람 날짜 추가
	 * @param gatherId
	 * @param request
	 * @return
	 */
	@PostMapping("/{gatherId}/exhibitions")
	public ResponseEntity<List<GatheringExhView>> addExhAboutGathering(
		@PathVariable(name = "gatherId") Long gatherId,
		@Valid @RequestBody AddExhDateRequest request
	) {
		// request body 데이터 받아오기
		var command = GatheringOperationUseCase.ExhGatheringCreateCommand.builder()
			.gatherId(gatherId)
			.exhId(request.getExhId())
			.visitDate(request.getVisitDate())
			.build();
		// 비즈니스 로직 호출
		List<GatheringReadUseCase.FindGatheringExhsResult> results = gatheringOperationUseCase.addExhAboutGathering(
			command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<GatheringExhView> viewResult = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringExhsResult result : results) {
			viewResult.add(GatheringExhView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}
}
