package klieme.artdiary.exhibition.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.enums.RegExhState;
import klieme.artdiary.exhibition.service.RegExhOperationUseCase;
import klieme.artdiary.exhibition.service.RegExhReadUseCase;
import klieme.artdiary.exhibition.ui.request_body.RegExhByAdminRequest;
import klieme.artdiary.exhibition.ui.request_body.RegExhByUserRequest;
import klieme.artdiary.exhibition.ui.view.RegExhListView;
import klieme.artdiary.exhibition.ui.view.RegExhView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/regexh")
public class RegExhController {
	private final RegExhReadUseCase regExhReadUseCase;
	private final RegExhOperationUseCase regExhOperationUseCase;

	@Autowired
	public RegExhController(RegExhReadUseCase regExhReadUseCase, RegExhOperationUseCase regExhOperationUseCase) {
		this.regExhReadUseCase = regExhReadUseCase;
		this.regExhOperationUseCase = regExhOperationUseCase;
	}

	@PostMapping("")
	public ResponseEntity<RegExhView> registerExhibitionByUser(
		@Valid @ModelAttribute RegExhByUserRequest request) {
		log.info("[등록할 전시회 추가(사용자)]");

		// request body 데이터 받아오기
		var command = RegExhOperationUseCase.RegExhCreateUpdateByUserCommand.builder()
			.regExhName(request.getRegExhName())
			.regGallery(request.getRegGallery())
			.regExhPeriodStart(request.getRegExhPeriodStart())
			.regExhPeriodEnd(request.getRegExhPeriodEnd())
			.regFee(request.getRegFee())
			.regUrl(request.getRegUrl())
			.regPoster(request.getRegPoster())
			.regDate(request.getRegDate())
			.build();
		// 비즈니스 로직 호출
		RegExhReadUseCase.FindRegExhResult regExhResult = regExhOperationUseCase.createRegExhByUser(command);

		return ResponseEntity.created(null).body(RegExhView.builder().result(regExhResult).build());
	}

	@GetMapping("")
	public ResponseEntity<List<RegExhListView>> getRegisteredExhibitionList(
		@RequestParam(name = "isAdmin") Boolean isAdmin
	) {
		log.info("[등록할 전시회 목록 조회(사용자/관리자)]");

		// 비즈니스 로직 호출
		List<RegExhReadUseCase.FindRegExhListResult> regExhListResults = regExhReadUseCase.getRegisteredExhibitionList(
			isAdmin);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<RegExhListView> results = new ArrayList<>();

		for (RegExhReadUseCase.FindRegExhListResult regExhList : regExhListResults) {
			results.add(RegExhListView.builder().result(regExhList).build());
		}
		return ResponseEntity.ok(results);
	}

	@GetMapping("/{regExhId}")
	public ResponseEntity<RegExhView> getRegisteredExhibition(
		@PathVariable(name = "regExhId") Long regExhId,
		@RequestParam(name = "isAdmin") Boolean isAdmin
	) {
		log.info("[등록할 전시회 하나 조회(사용자)]");

		// 비즈니스 로직 호출
		RegExhReadUseCase.FindRegExhResult regExhResult = regExhReadUseCase.getRegisteredExhibition(regExhId, isAdmin);

		return ResponseEntity.ok(RegExhView.builder().result(regExhResult).build());
	}

	@PatchMapping("/{regExhId}")
	public ResponseEntity<RegExhView> updateRegisteredExhibitionByUser(
		@PathVariable(name = "regExhId") Long regExhId,
		@Valid @ModelAttribute RegExhByUserRequest request
	) {
		log.info("[등록할 전시회 수정(사용자)]");

		var command = RegExhOperationUseCase.RegExhCreateUpdateByUserCommand.builder()
			.regExhId(regExhId)
			.regExhName(request.getRegExhName())
			.regGallery(request.getRegGallery())
			.regExhPeriodStart(request.getRegExhPeriodStart())
			.regExhPeriodEnd(request.getRegExhPeriodEnd())
			.regFee(request.getRegFee())
			.regUrl(request.getRegUrl())
			.regPoster(request.getRegPoster())
			.regDate(request.getRegDate())
			.build();
		// 비즈니스 로직 호출
		RegExhReadUseCase.FindRegExhResult regExhResult = regExhOperationUseCase.updateRegExhByUser(command);

		return ResponseEntity.ok(RegExhView.builder().result(regExhResult).build());
	}

	@DeleteMapping("/{regExhId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRegisteredExhibition(@PathVariable(name = "regExhId") Long regExhId) {
		log.info("[등록할 전시회 삭제(사용자)]");

		regExhOperationUseCase.deleteRegExhByUser(regExhId);
	}

	@PatchMapping("/{regExhId}/comments")
	public ResponseEntity<RegExhView> confirmExhibitionRequestByAdmin(
		@PathVariable(name = "regExhId") Long regExhId,
		@Valid @ModelAttribute RegExhByAdminRequest request
	) {
		log.info("[전시회 등록 요청 확인 코멘트 추가 및 전시회 정보 수정 (관리자)]");

		if (RegExhState.valueOfLabel(request.getRegState()) == null) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}

		var command = RegExhOperationUseCase.RegExhUpdateByAdminCommand.builder()
			.regExhId(regExhId)
			.regExhName(request.getRegExhName())
			.regGallery(request.getRegGallery())
			.regExhPeriodStart(request.getRegExhPeriodStart())
			.regExhPeriodEnd(request.getRegExhPeriodEnd())
			.regPainter(request.getRegPainter())
			.regFee(request.getRegFee())
			.regIntro(request.getRegIntro())
			.regUrl(request.getRegUrl())
			.regPoster(request.getRegPoster())
			.regArt(request.getRegArt())
			.regComment(request.getRegComment())
			.regState(RegExhState.valueOfLabel(request.getRegState()))
			.regSource(request.getRegSource())
			.build();
		// 비즈니스 로직 호출
		RegExhReadUseCase.FindRegExhResult regExhResult = regExhOperationUseCase.confirmExhRequestByAdmin(command);

		return ResponseEntity.ok(RegExhView.builder().result(regExhResult).build());
	}
}
