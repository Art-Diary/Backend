package klieme.artdiary.qna.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.qna.service.QnaOperationUseCase;
import klieme.artdiary.qna.service.QnaReadUseCase;
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
}

