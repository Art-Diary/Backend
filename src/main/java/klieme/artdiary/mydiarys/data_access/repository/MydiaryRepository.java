package klieme.artdiary.mydiarys.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;

@Repository
public interface MydiaryRepository extends JpaRepository<MydiaryEntity, Long> {
	List<MydiaryEntity> findByUserExhId(Long userExhId);

	Optional<MydiaryEntity> findBySoloDiaryId(Long soloDiaryId);

}
