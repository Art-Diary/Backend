package klieme.artdiary.qna.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.qna.data_access.entity.QnaEntity;
import klieme.artdiary.qna.data_access.repository.QnaRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.enums.RoleType;

@Service
public class QnaService implements QnaReadUseCase, QnaOperationUseCase {
	private final QnaRepository qnaRepository;

	@Autowired
	public QnaService(QnaRepository qnaRepository) {
		this.qnaRepository = qnaRepository;
	}

	@Transactional
	@Override
	public List<FindQnaResult> createQuestion(QnaCreateCommand command) {
		Long userId = getUserId();
		// 추가하기
		QnaEntity newQna = QnaEntity.builder()
			.userId(userId)
			.title(command.getTitle())
			.body(command.getBody())
			.state(false)
			.writeDate(command.getWriteDate())
			.build();
		qnaRepository.save(newQna);
		// 목록 조회하기
		List<QnaEntity> qnaEntityList = qnaRepository.findByUserId(userId);
		List<FindQnaResult> results = new ArrayList<>();

		for (QnaEntity qnaEntity : qnaEntityList) {
			results.add(FindQnaResult.findByQna(qnaEntity));
		}
		results = results.reversed();
		return results;
	}

	@Override
	public List<FindQnaResult> getQnaList(Boolean isAdmin) {
		UserEntity user = getUser();
		List<FindQnaResult> results = new ArrayList<>();
		List<QnaEntity> qnaEntityList;

		if (isAdmin) {
			// 관리자의 경우 (모두 보여주기)
			if (!Objects.equals(user.getRoleType(), RoleType.ADMIN.label())) {
				throw new ArtDiaryException(MessageType.FORBIDDEN);
			}
			qnaEntityList = qnaRepository.findAll();
		} else {
			// 사용자의 경우 (사용자 자신의 것만 보여주기)
			qnaEntityList = qnaRepository.findByUserId(user.getUserId());
		}
		for (QnaEntity qnaEntity : qnaEntityList) {
			results.add(FindQnaResult.findByQna(qnaEntity));
		}
		results = results.reversed();
		return results;
	}

	@Override
	public FindQnaResult getQnaDetail(Boolean isAdmin, Long qnaId) {

		UserEntity user = getUser();
		QnaEntity qnaEntity;
		if (isAdmin) {
			// 관리자의 경우 (모두 보여주기)
			if (!Objects.equals(user.getRoleType(), RoleType.ADMIN.label())) {
				throw new ArtDiaryException(MessageType.FORBIDDEN);
			}
			qnaEntity = qnaRepository.findByQnaId(qnaId)
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		} else {
			// 사용자의 경우 (사용자 자신의 것만 보여주기)
			qnaEntity = qnaRepository.findByQnaIdAndUserId(qnaId, user.getUserId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		}

		return FindQnaResult.findByQna(qnaEntity);
	}

	@Transactional
	@Override
	public QnaReadUseCase.FindQnaResult updateQnaContent(QnaUpdateCommand command, Long qnaId) {
		QnaEntity qnaEntity = qnaRepository.findByQnaIdAndUserId(qnaId, getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 답변이 달린 경우는 수정 불가능
		if (qnaEntity.getState()) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}

		QnaEntity updateQna = QnaEntity.builder()
			.qnaId(qnaEntity.getQnaId())
			.userId(qnaEntity.getUserId())
			.title(command.getTitle())
			.body(command.getBody())
			.state(false)
			.writeDate(command.getWriteDate())
			.build();
		qnaRepository.save(updateQna);

		return FindQnaResult.findByQna(updateQna);
	}

	@Transactional
	@Override
	public void deleteQuestion(Long qnaId) {
		QnaEntity qnaEntity = qnaRepository.findByQnaIdAndUserId(qnaId, getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 답변이 달린 경우는 삭제 불가능
		if (qnaEntity.getState()) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
		qnaRepository.delete(qnaEntity);
	}

	@Transactional
	@Override
	public FindQnaResult answerQnaByAdmin(QnaAnswerUpdateCommand command) {
		// 관리자인지 확인
		UserEntity user = getUser();

		if (!Objects.equals(user.getRoleType(), RoleType.ADMIN.label())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
		// 질문에 대답 추가/수정
		QnaEntity qnaEntity = qnaRepository.findByQnaId(command.getQnaId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		qnaEntity.updateAnswer(QnaEntity.builder()
			.answer(command.getAnswer())
			.answerDate(command.getAnswerDate())
			.state(true)
			.build());
		qnaRepository.save(qnaEntity);
		return FindQnaResult.findByQna(qnaEntity);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private UserEntity getUser() {
		return getCurrentUserEntity();
	}
}
