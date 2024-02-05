package klieme.artdiary.gatherings.ui.controller;

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
import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import klieme.artdiary.exhibitions.ui.view.StoredDateView;
import klieme.artdiary.gatherings.service.GatheringOperationUseCase;
import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import klieme.artdiary.gatherings.ui.request_body.AddGatheringRequest;
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
		var command = GatheringOperationUseCase.GatheringCreateCommand.builder()
			.gatherName(request.getGatherName())
			.build();
		GatheringReadUseCase.FindGatheringResult result = gatheringOperationUseCase.createGathering(command);
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
}
