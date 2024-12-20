package klieme.artdiary.qna.data_access.entity;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qna")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class QnaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "qna_id", nullable = false)
	private Long qnaId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private String body;
	@Column(nullable = false)
	private Boolean state;
	private String answer;
	@Column(name = "write_date", nullable = false)
	private LocalDate writeDate;
	@Column(name = "answer_date")
	private LocalDate answerDate;

	@Builder
	public QnaEntity(Long qnaId, Long userId, String title, String body, Boolean state, String answer,
		LocalDate writeDate,
		LocalDate answerDate) {
		this.qnaId = qnaId;
		this.userId = userId;
		this.title = title;
		this.body = body;
		this.state = state;
		this.answer = answer;
		this.writeDate = writeDate;
		this.answerDate = answerDate;
	}

	public void updateAnswer(QnaEntity qna) {
		if (qna.getAnswer() != null) {
			this.answer = qna.getAnswer();
		}
		if (qna.getAnswerDate() != null) {
			this.answerDate = qna.getAnswerDate();
		}
		if (qna.getState() != null) {
			this.state = qna.getState();
		}
	}
}
