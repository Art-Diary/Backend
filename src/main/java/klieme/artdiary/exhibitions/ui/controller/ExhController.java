package klieme.artdiary.exhibitions.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.exhibitions.service.ExhOperationUseCase;
import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import klieme.artdiary.exhibitions.ui.request_body.AddSoloExhRequest;
import klieme.artdiary.exhibitions.ui.request_body.ExhRequest;
import klieme.artdiary.exhibitions.ui.view.StoredDateView;

@RestController
@RequestMapping(value = "/exhibitions")
public class ExhController {

	private final ExhOperationUseCase exhOperationUseCase;
	private final ExhReadUseCase exhReadUseCase;

	@Autowired
	public ExhController(ExhOperationUseCase exhOperationUseCase, ExhReadUseCase exhReadUseCase) {
		this.exhOperationUseCase = exhOperationUseCase;
		this.exhReadUseCase = exhReadUseCase;
	}

	@GetMapping("/hello")
	public void helloPrint() {
		System.out.println("hello");
	}

	@PostMapping("")
	public void createDummyDate(@Valid @RequestBody ExhRequest exhRequest) {

		System.out.println("Test");

		var command = ExhOperationUseCase.ExhDummyCreateCommand.builder()
			.exhName(exhRequest.getExhName())
			.gallery(exhRequest.getGallery())
			.exhPeriodStart(exhRequest.getExhPeriodStart())
			.exhPeriodEnd(exhRequest.getExhPeriodEnd())
			.painter(exhRequest.getPainter())
			.fee(exhRequest.getFee())
			.intro(exhRequest.getIntro())
			.url(exhRequest.getUrl())
			.poster(exhRequest.getPoster())
			.build();

		System.out.println(exhOperationUseCase.createDummy(command));
	}

	@PostMapping("/{exhId}")
	public ResponseEntity<StoredDateView> addSoloExhCreateDummyDate(@PathVariable(name = "exhId") Long exhId,
		@Valid @RequestBody AddSoloExhRequest addSoloExhRequest) {

		System.out.println("Test");

		var command = ExhOperationUseCase.AddSoloExhDummyCreateCommand.builder()
			.visitDate(addSoloExhRequest.getVisitDate())
			.exhId(exhId)
			.build();

		ExhReadUseCase.FindStoredDateResult result = exhOperationUseCase.addSoloExhCreateDummy(command);

		// System.out.println(exhOperationUseCase.addSoloExhCreateDummy(command));
		return ResponseEntity.created(null).body(StoredDateView.builder().result(result).build());
	}

	@GetMapping("/{exhId}/date") // ResponseEntity<>
	public ResponseEntity<StoredDateView> getStoredDateOfExhs(
		@PathVariable(name = "exhId") Long exhId,
		@RequestParam(name = "gatherId", required = false) Long gatherId
	) {
		var query = ExhReadUseCase.StoredDateFindQuery.builder()
			.exhId(exhId)
			.gatherId(gatherId)
			.build();
		ExhReadUseCase.FindStoredDateResult result = exhReadUseCase.getStoredDateOfExhs(query);

		return ResponseEntity.ok(StoredDateView.builder().result(result).build());
	}
}
