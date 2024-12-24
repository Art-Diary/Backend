package klieme.artdiary.qna.service;

import static klieme.artdiary.common.FormatDate.*;

import java.util.List;

import klieme.artdiary.qna.data_access.entity.QnaEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface QnaReadUseCase {
	List<FindQnaResult> getQnaList(Boolean isAdmin);

	FindQnaResult getQnaDetail(Boolean isAdmin, Long qnaId);

	@Getter
	@ToString
	@Builder
	class FindQnaResult {
		private final Long qnaId;
		private final String title;
		private final String body;
		private final Long userId;
		private final Boolean state;
		private final String answer;
		private final String writeDate;
		private final String answerDate;

		public static FindQnaResult findByQna(QnaEntity qna) {
			return FindQnaResult.builder()
				.qnaId(qna.getQnaId())
				.title(qna.getTitle())
				.body(qna.getBody())
				.userId(qna.getUserId())
				.state(qna.getState())
				.answer(qna.getAnswer())
				.writeDate(changeDateFormat(qna.getWriteDate()))
				.answerDate(changeDateFormat(qna.getAnswerDate()))
				.build();
		}
	}
}
