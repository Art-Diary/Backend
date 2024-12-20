package klieme.artdiary.qna.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.qna.service.QnaReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QnaView {
	private final Long qnaId;
	private final String title;
	private final String body;
	private final Long userId;
	private final Boolean state;
	private final String answer;
	private final String writeDate;
	private final String answerDate;

	@Builder
	public QnaView(QnaReadUseCase.FindQnaResult result) {
		this.qnaId = result.getQnaId();
		this.title = result.getTitle();
		this.body = result.getBody();
		this.userId = result.getUserId();
		this.state = result.getState();
		this.answer = result.getAnswer();
		this.writeDate = result.getWriteDate();
		this.answerDate = result.getAnswerDate();
	}
}
