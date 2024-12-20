package klieme.artdiary.qna.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.qna.service.QnaOperationUseCase;
import klieme.artdiary.qna.service.QnaReadUseCase;
import klieme.artdiary.qna.ui.request_body.QnaAnswerRequest;
import klieme.artdiary.qna.ui.request_body.QnaRequest;
import klieme.artdiary.qna.ui.view.QnaView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/qna")
public class QnaController {
	private final QnaReadUseCase qnaReadUseCase;
	private final QnaOperationUseCase qnaOperationUseCase;

	@Autowired
	public QnaController(QnaReadUseCase qnaReadUseCase, QnaOperationUseCase qnaOperationUseCase) {
		this.qnaReadUseCase = qnaReadUseCase;
		this.qnaOperationUseCase = qnaOperationUseCase;
	}

	/**
	 * 질문 추가
	 */
	@PostMapping("")
	public ResponseEntity<List<QnaView>> createQna(@Valid @RequestBody QnaRequest request) {
		log.info("[Q&A 질문 추가]");

		var command = QnaOperationUseCase.QnaCreateCommand.builder()
			.title(request.getTitle())
			.body(request.getBody())
			.writeDate(request.getWriteDate())
			.build();

		List<QnaReadUseCase.FindQnaResult> qnaResults = qnaOperationUseCase.createQuestion(command);
		List<QnaView> results = new ArrayList<>();

		for (QnaReadUseCase.FindQnaResult qnaResult : qnaResults) {
			results.add(QnaView.builder().result(qnaResult).build());
		}
		return ResponseEntity.created(null).body(results);
	}

	/**
	 * qna 리스트
	 */
	@GetMapping("")
	public ResponseEntity<List<QnaView>> getQnaList(@RequestParam(name = "isAdmin") Boolean isAdmin) {
		log.info("[Q&A 리스트 조회]");

		List<QnaReadUseCase.FindQnaResult> qnaResults = qnaReadUseCase.getQnaList(isAdmin);
		List<QnaView> result = new ArrayList<>();

		for (QnaReadUseCase.FindQnaResult qnaResult : qnaResults) {
			result.add(QnaView.builder().result(qnaResult).build());
		}
		return ResponseEntity.ok(result);
	}

	/**
	 * qna 삭제
	 */
	@DeleteMapping("/{qnaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQna(@PathVariable(name = "qnaId") Long qnaId) {
		log.info("[Q&A 질문 삭제]");

		qnaOperationUseCase.deleteQuestion(qnaId);
	}

	/**
	 * qna 관리자 답변 작성/수정
	 */
	@PatchMapping("/{qnaId}/answer")
	public ResponseEntity<QnaView> answerQnaByAdmin(@PathVariable(name = "qnaId") Long qnaId,
		@Valid @RequestBody QnaAnswerRequest request) {
		log.info("[Q&A 관리자 답변 작성/수정]");

		var command = QnaOperationUseCase.QnaAnswerUpdateCommand.builder()
			.qnaId(qnaId)
			.answer(request.getAnswer())
			.answerDate(request.getAnswerDate())
			.build();

		QnaReadUseCase.FindQnaResult result = qnaOperationUseCase.answerQnaByAdmin(command);

		return ResponseEntity.ok(QnaView.builder().result(result).build());
	}
}

