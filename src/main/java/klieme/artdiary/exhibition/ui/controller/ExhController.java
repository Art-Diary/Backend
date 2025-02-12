package klieme.artdiary.exhibition.ui.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import klieme.artdiary.exhibition.service.ExhOperationUseCase;
import klieme.artdiary.exhibition.service.ExhReadUseCase;
import klieme.artdiary.exhibition.ui.request_body.ExhRequest;
import klieme.artdiary.exhibition.ui.view.AllDiaryOfExhIdView;
import klieme.artdiary.exhibition.ui.view.ExhView;
import klieme.artdiary.exhibition.ui.view.StoredDateView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	//[here/hw]
	@GetMapping("/{exhId}/date") // ResponseEntity<>
	public ResponseEntity<StoredDateView> getStoredDateOfExhsByGatherId(
		@PathVariable(name = "exhId") Long exhId,
		@RequestParam(name = "gatherId", required = false) Long gatherId
	) {
		log.info("[한 전시회에 대해 캘린더에 저장된 날짜 조회]");
		var query = ExhReadUseCase.StoredDateFindQuery.builder()
			.exhId(exhId)
			.gatherId(gatherId)
			.build();
		ExhReadUseCase.FindStoredDateResult result = exhReadUseCase.getStoredDateOfExhsByGatherId(query);

		return ResponseEntity.ok(StoredDateView.builder().result(result).build());
	}

	@GetMapping("/{exhId}")
	public ResponseEntity<ExhView> getExhDetailInfo(@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시회 상세 정보 조회]");

		ExhReadUseCase.FindExhResult result = exhReadUseCase.getExhDetailInfo(exhId);

		return ResponseEntity.ok(ExhView.builder().result(result).build());

	}

	//[here/hw]
	@GetMapping("/{exhId}/diaries")
	public ResponseEntity<List<AllDiaryOfExhIdView>> getAllOfExhIdDiaries(@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시회 상세 정보 중 기록 조회]");

		List<ExhReadUseCase.FindDiaryResult> diaryResults = exhReadUseCase.getAllOfExhIdDiaries(exhId);

		List<AllDiaryOfExhIdView> result = new ArrayList<>();

		for (ExhReadUseCase.FindDiaryResult diaryResult : diaryResults) {
			result.add(AllDiaryOfExhIdView.builder().result(diaryResult).build());
		}
		return ResponseEntity.ok(result);

	}

	@GetMapping("")//-ing
	public ResponseEntity<List<ExhView>> getExhList(
		@RequestParam(name = "searchName", required = false) String searchName, //검색 내용
		@RequestParam(name = "field", required = false) String[] fieldList, // 전시 분야
		@RequestParam(name = "price", required = false) String price, // 가격
		@RequestParam(name = "state", required = false) String[] stateList, // 전시 오픈 상태
		@RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date // 날짜
	) {
		log.info("[전시회 목록 조회(+전시회 검색, 좋아요 조회)]");

		// string 자료형을 갖는 변수일 경우 빈 문자열인지 확인
		if ((searchName != null && searchName.isBlank()) || (fieldList != null && fieldList.length == 0) || (
			price != null && price.isBlank()) || (stateList != null && stateList.length == 0) || (stateList != null
			&& date != null)) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// field, state, price 각각 정해진 값이 들어왔는지 확인
		List<ExhField> fields = new ArrayList<>();
		List<ExhState> states = new ArrayList<>();
		if (fieldList != null) {
			for (String field : fieldList) {
				if (ExhField.valueOfLabel(field) == null) {
					throw new ArtDiaryException(MessageType.BAD_REQUEST);
				}
				fields.add(ExhField.valueOfLabel(field));
			}
		}
		if (stateList != null) {
			for (String state : stateList) {
				if (ExhState.valueOfLabel(state) == null) {
					throw new ArtDiaryException(MessageType.BAD_REQUEST);
				}
				states.add(ExhState.valueOfLabel(state));
			}
		}
		if ((price != null && ExhPrice.valueOfLabel(price) == null)) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		var query = ExhReadUseCase.ExhListFindQuery.builder()
			.searchName(searchName)
			.fieldList(fields)
			.stateList(states)
			.price(ExhPrice.valueOfLabel(price))
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

	@GetMapping("/search")
	public ResponseEntity<List<ExhView>> getExhListBySearchName(
		@NotBlank @RequestParam(name = "searchName") String searchName) {

		log.info("[전시회 이름 검색 결과 조회]");

		// 비즈니스 로직 호출
		List<ExhReadUseCase.FindExhResult> exhResults = exhReadUseCase.getExhListBySearchName(
			searchName);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<ExhView> result = new ArrayList<>();

		for (ExhReadUseCase.FindExhResult exhResult : exhResults) {
			result.add(ExhView.builder().result(exhResult).build());
		}
		return ResponseEntity.ok(result);
	}

	@PatchMapping("/{exhId}")
	public ResponseEntity<ExhView> updateExhDetailInfo(@PathVariable(name = "exhId") Long exhId,
		@Valid @ModelAttribute ExhRequest exhRequest) {
		log.info("[전시회 상세 정보 업데이트]");

		// request body 데이터 받아오기
		var command = ExhOperationUseCase.ExhUpdateCommand.builder()
			.exhId(exhId)
			.exhName(exhRequest.getExhName())
			.gallery(exhRequest.getGallery())
			.exhPeriodStart(exhRequest.getExhPeriodStart())
			.exhPeriodEnd(exhRequest.getExhPeriodEnd())
			.painter(exhRequest.getPainter())
			.fee(exhRequest.getFee())
			.intro(exhRequest.getIntro())
			.url(exhRequest.getUrl())
			.poster(exhRequest.getPoster())
			.art(exhRequest.getArt())
			.source(exhRequest.getSource())
			.build();
		// 비즈니스 로직 호출
		ExhReadUseCase.FindExhResult result = exhOperationUseCase.updateExhDetailInfo(command);

		return ResponseEntity.ok(ExhView.builder().result(result).build());
	}
}
