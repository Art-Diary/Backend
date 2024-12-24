package klieme.artdiary.qna.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface QnaOperationUseCase {
	List<QnaReadUseCase.FindQnaResult> createQuestion(QnaCreateCommand command);

	void deleteQuestion(Long qnaId);

	QnaReadUseCase.FindQnaResult answerQnaByAdmin(QnaAnswerUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class QnaCreateCommand {
		private final String title;
		private final String body;
		private final LocalDate writeDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class QnaAnswerUpdateCommand {
		private final Long qnaId;
		private final String answer;
		private final LocalDate answerDate;
	}
}
