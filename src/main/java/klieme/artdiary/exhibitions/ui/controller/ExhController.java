package klieme.artdiary.exhibitions.ui.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.exhibitions.enums.ExhField;
import klieme.artdiary.exhibitions.enums.ExhPrice;
import klieme.artdiary.exhibitions.enums.ExhState;
import klieme.artdiary.exhibitions.service.ExhOperationUseCase;
import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import klieme.artdiary.exhibitions.ui.request_body.ExhRequest;
import klieme.artdiary.exhibitions.ui.view.AllDiaryOfExhIdView;
import klieme.artdiary.exhibitions.ui.view.ExhView;
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

	/*
		@PostMapping("/{exhId}") //캘린더저장
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
	*/
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

	@GetMapping("/{exhId}")
	public ResponseEntity<ExhView> getExhDetailInfo(@PathVariable(name = "exhId") Long exhId) {

		ExhReadUseCase.FindExhResult result = exhReadUseCase.getExhDetailInfo(exhId);

		return ResponseEntity.ok(ExhView.builder().result(result).build());

	}

	@GetMapping("/{exhId}/diaries")
	public ResponseEntity<List<AllDiaryOfExhIdView>> getAllOfExhIdDiaries(@PathVariable(name = "exhId") Long exhId) {

		List<ExhReadUseCase.FindDiaryResult> diaryResults = exhReadUseCase.getAllOfExhIdDiaries(exhId);

		List<AllDiaryOfExhIdView> result = new ArrayList<>();

		for (ExhReadUseCase.FindDiaryResult diaryResult : diaryResults) {
			result.add(AllDiaryOfExhIdView.builder().result(diaryResult).build());
		}
		return ResponseEntity.ok(result);

	}

	@GetMapping("")
	public ResponseEntity<List<ExhView>> getExhList(
		@RequestParam(name = "searchName", required = false) String searchName,
		@RequestParam(name = "field", required = false) String field,
		@RequestParam(name = "price", required = false) String price,
		@RequestParam(name = "state", required = false) String state,
		@RequestParam(name = "date", required = false) LocalDate date
	) {
		// 요청 파라미터 검증 => 조합: 1. (searchName) 2. (field, price, state) 3. (date)
		if (!(searchName == null && field == null && price == null && state == null && date == null) &&
			!((searchName == null && (field != null || price != null || state != null) && date == null)
				|| ((searchName != null && !searchName.isBlank()) && field == null && price == null && state == null
				&& date == null)
				|| (searchName == null && field == null && price == null && state == null && date != null))) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// field, state => 정해진 문자열이 들어왔는지 확인
		if ((field != null && ExhField.valueOfLabel(field) == null)
			|| (state != null && ExhState.valueOfLabel(state) == null)
			|| (price != null && ExhPrice.valueOfLabel(price) == null)) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// 조합 2번 형태의 클래스 자료형을 만들어 사용
		ExhReadUseCase.ExhListFindQuery.ExhCategory exhCategory;
		if (field != null || price != null || state != null) {
			exhCategory = ExhReadUseCase.ExhListFindQuery.ExhCategory.builder()
				.field(ExhField.valueOfLabel(field))
				.price(ExhPrice.valueOfLabel(price))
				.state(ExhState.valueOfLabel(state))
				.build();
		} else {
			exhCategory = null;
		}
		// 파라미터로 받은 데이터 service로 전달하기 위함.
		var query = ExhReadUseCase.ExhListFindQuery.builder()
			.searchName(searchName)
			.exhCategory(exhCategory)
			.date(date)
			.build();
		// 비즈니스 로직 호출
		List<ExhReadUseCase.FindExhResult> exhResults = exhReadUseCase.getExhList(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<ExhView> result = new ArrayList<>();

		for (ExhReadUseCase.FindExhResult exhResult : exhResults) {
			result.add(ExhView.builder().result(exhResult).build());
		}
		return ResponseEntity.ok(result);
	}
}
