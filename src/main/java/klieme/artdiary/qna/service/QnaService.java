package klieme.artdiary.qna.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.qna.data_access.repository.QnaRepository;

@Service
public class QnaService implements QnaReadUseCase, QnaOperationUseCase {
	private final QnaRepository qnaRepository;

	@Autowired
	public QnaService(QnaRepository qnaRepository) {
		this.qnaRepository = qnaRepository;
	}
}
