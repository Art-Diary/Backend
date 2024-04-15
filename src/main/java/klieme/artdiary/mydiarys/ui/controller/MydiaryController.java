package klieme.artdiary.mydiarys.ui.controller;

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
import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.mydiarys.service.MydiaryOperationUseCase;
import klieme.artdiary.mydiarys.service.MydiaryReadUseCase;
import klieme.artdiary.mydiarys.ui.request_body.MyDiaryUpdateRequest;
import klieme.artdiary.mydiarys.ui.request_body.MydiaryRequest;
import klieme.artdiary.mydiarys.ui.view.MydiaryView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/myexhs/{exhId}/diaries")
public class MydiaryController {
	private final MydiaryOperationUseCase mydiaryOperationUseCase;
	private final MydiaryReadUseCase mydiaryReadUseCase;

	@Autowired
	public MydiaryController(MydiaryOperationUseCase mydiaryOperationUseCase, MydiaryReadUseCase mydiaryReadUseCase) {
		this.mydiaryOperationUseCase = mydiaryOperationUseCase;
		this.mydiaryReadUseCase = mydiaryReadUseCase;
	}

	/**
	 *기록 추가
	 * "/myexhs/:exhId/diaries"
	 */
	@PostMapping("")
	public ResponseEntity<List<MydiaryView>> createDiary(
		@PathVariable(name = "exhId") Long exhId,
		@Valid @ModelAttribute MydiaryRequest request
	) throws IOException {
		log.info("[기록 추가]");
		if (!((request.getUserExhId() == -1 && request.getGatheringExhId() != -1)
			|| (request.getUserExhId() != -1 && request.getGatheringExhId() == -1))) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// request body 데이터 받아오기
		var command = MydiaryOperationUseCase.MyDiaryCreateUpdateCommand.builder()
			.exhId(exhId)
			.userExhId(request.getUserExhId())
			.gatheringExhId(request.getGatheringExhId())
			.title(request.getTitle())
			.rate(request.getRate())
			.diaryPrivate(request.getDiaryPrivate())
			.contents(request.getContents())
			.thumbnail(request.getThumbnail())
			.writeDate(request.getWriteDate())
			.saying(request.getSaying())
			.build();
		// 비즈니스 로직 호출
		List<MydiaryReadUseCase.FindMyDiaryResult> myDiaryResults = mydiaryOperationUseCase.createMyDiary(command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MydiaryView> results = new ArrayList<>();

		for (MydiaryReadUseCase.FindMyDiaryResult myDiaryResult : myDiaryResults) {
			results.add(MydiaryView.builder().result(myDiaryResult).build());
		}
		return ResponseEntity.created(null).body(results);
	}

	/**
	 * 기록 목록 조회
	 * "/myexhs/:exhId/diaries"
	 */
	@GetMapping("")
	public ResponseEntity<List<MydiaryView>> getDiaries(@PathVariable(name = "exhId") Long exhId,
		@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "date", required = false) LocalDate date,
		@RequestParam(name = "forget", required = false) Boolean forget,
		@RequestParam(name = "gatheringExhId", required = false) Long gatheringExhId) throws IOException {

		log.info("[기록 목록 조회]");

		// request parameter 확인
		// forget이 null이면 날짜를 적용하지 않은 api로 인식.
		// forget이 null이 아니면 forget=true/false에 따라 date 값 확인
		// forget이 true일 때 date 값이 null로 설정되어 '기억안남'으로 인식
		// forget이 false일 때 date 값이 null이 아닌 날짜 값이 들어있어 요청한 날짜에 대한 api로 인식.
		if (forget != null && ((forget && date != null) || (!forget && date == null))) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// forget이 널일때 date나 gatheringExhId도 널이어야한다.
		if (forget == null && (date != null || gatheringExhId != null)) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}

		var query = MydiaryReadUseCase.MyDiariesFindQuery.builder()
			.exhId(exhId)
			.forget(forget)
			.date(forget == null ? null : date)
			.gatheringExhId(forget == null ? null : gatheringExhId)
			.build();
		// 비즈니스 로직 호출
		List<MydiaryReadUseCase.FindMyDiaryResult> myDiaryResults = mydiaryReadUseCase.getMyDiaries(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MydiaryView> results = new ArrayList<>();

		for (MydiaryReadUseCase.FindMyDiaryResult myDiaryResult : myDiaryResults) {
			results.add(MydiaryView.builder().result(myDiaryResult).build());
		}
		return ResponseEntity.ok(results);
	}

	@DeleteMapping("/{diaryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDiary(@PathVariable(name = "exhId") Long exhId, @PathVariable(name = "diaryId") Long diaryId,
		@RequestParam(name = "solo") Boolean solo) {
		log.info("[기록 삭제]");

		mydiaryOperationUseCase.deleteMyDiary(exhId, solo, diaryId);

	}

	/**
	 * 기록 수정
	 * "/myexhs/:exhId/diaries/:diaryId"
	 */
	@PatchMapping("/{diaryId}")
	public ResponseEntity<List<MydiaryView>> updateMyDiary(
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "diaryId") Long diaryId,
		@Valid @ModelAttribute MyDiaryUpdateRequest request
	) throws IOException {
		log.info("[기록 수정]");
		if (!((request.getUserExhId() == -1 && request.getGatheringExhId() != -1)
			|| (request.getUserExhId() != -1 && request.getGatheringExhId() == -1))) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// request body 데이터 받아오기
		var command = MydiaryOperationUseCase.MyDiaryCreateUpdateCommand.builder()
			.exhId(exhId)
			.diaryId(diaryId)
			.userExhId(request.getUserExhId())
			.gatheringExhId(request.getGatheringExhId())
			.title(request.getTitle())
			.rate(request.getRate())
			.diaryPrivate(request.getDiaryPrivate())
			.contents(request.getContents())
			.thumbnail(request.getThumbnail())
			.writeDate(request.getWriteDate())
			.saying(request.getSaying())
			.build();
		// 비즈니스 로직 호출
		List<MydiaryReadUseCase.FindMyDiaryResult> myDiaryResults = mydiaryOperationUseCase.updateMyDiary(command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<MydiaryView> results = new ArrayList<>();

		for (MydiaryReadUseCase.FindMyDiaryResult myDiaryResult : myDiaryResults) {
			results.add(MydiaryView.builder().result(myDiaryResult).build());
		}
		return ResponseEntity.ok(results);
	}
}
