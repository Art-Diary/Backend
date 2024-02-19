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
import klieme.artdiary.gatherings.ui.request_body.AddGatheringMateRequest;
import klieme.artdiary.gatherings.ui.request_body.AddGatheringRequest;
import klieme.artdiary.gatherings.ui.view.GatheringDetailInfoView;
import klieme.artdiary.gatherings.ui.view.GatheringDiaryView;
import klieme.artdiary.gatherings.ui.view.GatheringExhView;
import klieme.artdiary.gatherings.ui.view.GatheringMateView;
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

	/**
	 * 한 전시회에 대한 모임 기록 목록 조회
	 */
	@GetMapping("/{gatherId}/exhibitions/{exhId}")
	public ResponseEntity<List<GatheringDiaryView>> getDiariesAboutGatheringExh(
		@PathVariable(name = "gatherId") Long gatherId,
		@PathVariable(name = "exhId") Long exhId
	) {
		var query = GatheringReadUseCase.GatheringDiariesFindQuery.builder()
			.exhId(exhId)
			.gatherId(gatherId)
			.build();
		// 비즈니스 로직 호출
		List<GatheringReadUseCase.FindGatheringDiaryResult> results = gatheringReadUseCase.getDiariesAboutGatheringExh(
			query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<GatheringDiaryView> viewResult = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringDiaryResult result : results) {
			viewResult.add(GatheringDiaryView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	@PostMapping("/{gatherId}")
	public ResponseEntity<List<GatheringMateView>> addGatheringMate(
		@PathVariable(name = "gatherId") Long gatherId,
		@Valid @RequestBody AddGatheringMateRequest request
	) {
		var command = GatheringOperationUseCase.GatheringMateCreateCommand.builder()
			.gatherId(gatherId)
			.userId(request.getUserId())
			.build();
		// 비즈니스 로직 호출
		List<GatheringReadUseCase.FindGatheringMatesResult> results = gatheringOperationUseCase.addGatheringMate(
			command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<GatheringMateView> viewList = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringMatesResult result : results) {
			viewList.add(GatheringMateView.builder().result(result).build());
		}
		return ResponseEntity.created(null).body(viewList);
	}

	/**
	 * 모임 상세 정보 조회(모임 멤버 + 갔다온 전시회 목록)
	 */
	@GetMapping("/{gatherId}")
	public ResponseEntity<GatheringDetailInfoView> getGatheringDetailInfo(
		@PathVariable(name = "gatherId") Long gatherId
	) {
		var query = GatheringReadUseCase.GatheringDetailInfoFindQuery.builder()
			.gatherId(gatherId)
			.build();
		// 비즈니스 로직 호출
		GatheringReadUseCase.FindGatheringDetailInfoResult detailInfoResult = gatheringReadUseCase.getGatheringDetailInfo(
			query);
		// 반환
		return ResponseEntity.ok(GatheringDetailInfoView.builder().result(detailInfoResult).build());
	}
}
