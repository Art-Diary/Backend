package klieme.artdiary.mydiarys.ui.controller;

import java.io.IOException;
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
import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.mydiarys.service.MydiaryOperationUseCase;
import klieme.artdiary.mydiarys.service.MydiaryReadUseCase;
import klieme.artdiary.mydiarys.ui.request_body.MyDiaryUpdateRequest;
import klieme.artdiary.mydiarys.ui.request_body.MydiaryRequest;
import klieme.artdiary.mydiarys.ui.view.MydiaryView;

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
	public ResponseEntity<List<MydiaryView>> getDiaries(@PathVariable(name = "exhId") Long exhId) throws IOException {
		var query = MydiaryReadUseCase.MyDiariesFindQuery.builder().exhId(exhId).build();
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
