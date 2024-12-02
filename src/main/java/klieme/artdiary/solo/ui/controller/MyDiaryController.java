package klieme.artdiary.solo.ui.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import klieme.artdiary.solo.service.MyDiaryOperationUseCase;
import klieme.artdiary.solo.service.MyDiaryReadUseCase;
import klieme.artdiary.solo.ui.request_body.MyDiaryRequest;
import klieme.artdiary.solo.ui.view.MyDiaryView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/myexhs/{exhId}/diaries")
public class MyDiaryController {
	private final MyDiaryOperationUseCase mydiaryOperationUseCase;
	private final MyDiaryReadUseCase mydiaryReadUseCase;

	@Autowired
	public MyDiaryController(MyDiaryOperationUseCase mydiaryOperationUseCase, MyDiaryReadUseCase mydiaryReadUseCase) {
		this.mydiaryOperationUseCase = mydiaryOperationUseCase;
		this.mydiaryReadUseCase = mydiaryReadUseCase;
	}

	/**
	 *기록 추가
	 * "/myexhs/:exhId/diaries"
	 */
	@PostMapping("")
	public ResponseEntity<List<MyDiaryView>> createDiary(
		@PathVariable(name = "exhId") Long exhId,
		@Valid @ModelAttribute MyDiaryRequest request
	) {
		log.info("[기록 추가]");
		// request body 데이터 받아오기
		var command = MyDiaryOperationUseCase.MyDiaryCreateUpdateCommand.builder()
			.exhId(exhId)
			.exhVisitId(request.getExhVisitId())
			.title(request.getTitle())
			.rate(request.getRate())
			.diaryPrivate(request.getDiaryPrivate())
			.contents(request.getContents())
			.thumbnail(request.getThumbnail())
			.writeDate(request.getWriteDate())
			.saying(request.getSaying())
			.files(request.getFiles())
			.build();
		// 비즈니스 로직 호출
		List<MyDiaryReadUseCase.FindMyDiaryResult> myDiaryResults = mydiaryOperationUseCase.createMyDiary(command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MyDiaryView> results = new ArrayList<>();

		for (MyDiaryReadUseCase.FindMyDiaryResult myDiaryResult : myDiaryResults) {
			results.add(MyDiaryView.builder().result(myDiaryResult).build());
		}
		return ResponseEntity.created(null).body(results);
	}

	/**
	 * 기록 목록 조회
	 * "/myexhs/:exhId/diaries"
	 */
	@GetMapping("")
	public ResponseEntity<List<MyDiaryView>> getDiaries(@PathVariable(name = "exhId") Long exhId,
		@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "visitDate", required = false) LocalDate visitDate,
		@RequestParam(name = "forget", required = false) Boolean forget,
		@RequestParam(name = "gatherId", required = false) Long gatherId) throws IOException {

		log.info("[기록 목록 조회]" + " forget: " + forget + " visitDate: " + visitDate + " gatherId: " + gatherId);

		// request parameter 확인
		// 1. 내 기록 조회: forget, visitDate, gatherId 없는 경우
		// 2. 캘린더 조회
		// 		- gatherId && visitdate
		// 		- solo && visitdate
		// 		- solo && forget=true
		if (!((gatherId != null && visitDate != null)
			|| (gatherId == null && visitDate != null)
			|| (gatherId == null && forget != null && forget)
			|| (gatherId == null && (forget == null || !forget) && visitDate == null))) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}

		var query = MyDiaryReadUseCase.MyDiariesFindQuery.builder()
			.exhId(exhId)
			.forget(forget != null && forget ? true : null)
			.visitDate(visitDate)
			.gatherId(gatherId)
			.build();
		// 비즈니스 로직 호출
		List<MyDiaryReadUseCase.FindMyDiaryResult> myDiaryResults = mydiaryReadUseCase.getMyDiaries(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MyDiaryView> results = new ArrayList<>();

		for (MyDiaryReadUseCase.FindMyDiaryResult myDiaryResult : myDiaryResults) {
			results.add(MyDiaryView.builder().result(myDiaryResult).build());
		}
		return ResponseEntity.ok(results);
	}

	@DeleteMapping("/{diaryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDiary(@PathVariable(name = "exhId") Long exhId, @PathVariable(name = "diaryId") Long diaryId) {
		log.info("[기록 삭제]");

		mydiaryOperationUseCase.deleteMyDiary(exhId, diaryId);

	}

	/**
	 * 기록 수정
	 * "/myexhs/:exhId/diaries/:diaryId"
	 */
	@PatchMapping("/{diaryId}")
	public ResponseEntity<List<MyDiaryView>> updateMyDiary(
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "diaryId") Long diaryId,
		@Valid @ModelAttribute MyDiaryRequest request
	) {
		log.info("[기록 수정]");
		// request body 데이터 받아오기
		var command = MyDiaryOperationUseCase.MyDiaryCreateUpdateCommand.builder()
			.exhId(exhId)
			.diaryId(diaryId)
			.exhVisitId(request.getExhVisitId())
			.title(request.getTitle())
			.rate(request.getRate())
			.diaryPrivate(request.getDiaryPrivate())
			.contents(request.getContents())
			.thumbnail(request.getThumbnail())
			.writeDate(request.getWriteDate())
			.saying(request.getSaying())
			.files(request.getFiles())
			.build();
		// 비즈니스 로직 호출
		List<MyDiaryReadUseCase.FindMyDiaryResult> myDiaryResults = mydiaryOperationUseCase.updateMyDiary(command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MyDiaryView> results = new ArrayList<>();

		for (MyDiaryReadUseCase.FindMyDiaryResult myDiaryResult : myDiaryResults) {
			results.add(MyDiaryView.builder().result(myDiaryResult).build());
		}
		return ResponseEntity.ok(results);
	}
}
