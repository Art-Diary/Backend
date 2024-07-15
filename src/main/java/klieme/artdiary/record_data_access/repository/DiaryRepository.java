package klieme.artdiary.record_data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.record_data_access.entity.DiaryEntity;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity, Long>, DiaryRepoCustom {
	Optional<DiaryEntity> findByDiaryId(Long diaryId);

	List<DiaryEntity> findByWriterId(Long writerId);
}
