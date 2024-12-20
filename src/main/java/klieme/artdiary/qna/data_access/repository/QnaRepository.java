package klieme.artdiary.qna.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.qna.data_access.entity.QnaEntity;

@Repository
public interface QnaRepository extends JpaRepository<QnaEntity, Long> {
	List<QnaEntity> findByUserId(Long userId);

	Optional<QnaEntity> findByQnaIdAndUserId(Long qnaId, Long userId);

	Optional<QnaEntity> findByQnaId(Long qnaId);
}
