package klieme.artdiary.gatherings.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.gatherings.service.GatheringOperationUseCase;
import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import klieme.artdiary.gatherings.ui.request_body.AddGatheringRequest;
import klieme.artdiary.gatherings.ui.view.GatheringView;

@RestController
@RequestMapping(value = "/gatherings")
public class GatheringController {
	private final GatheringOperationUseCase gatheringOperationUseCase;

	@Autowired
	public GatheringController(GatheringOperationUseCase gatheringOperationUseCase) {
		this.gatheringOperationUseCase = gatheringOperationUseCase;
	}

	@PostMapping("")
	public ResponseEntity<GatheringView> createGathering(@Valid @RequestBody AddGatheringRequest request) {
		var command = GatheringOperationUseCase.GatheringCreateCommand.builder()
			.gatherName(request.getGatherName())
			.build();
		GatheringReadUseCase.FindGatheringResult result = gatheringOperationUseCase.createGathering(command);
		return ResponseEntity.created(null).body(GatheringView.builder().result(result).build());
	}
}
