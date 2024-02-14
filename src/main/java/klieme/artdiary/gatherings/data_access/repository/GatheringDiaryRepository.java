package klieme.artdiary.gatherings.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;

@Repository
public interface GatheringDiaryRepository extends JpaRepository<GatheringDiaryEntity, Long> {
	List<GatheringDiaryEntity> findByGatheringExhId(Long gatheringExhId);

	List<GatheringDiaryEntity> findByUserId(Long userId);

	Optional<GatheringDiaryEntity> findByGatherDiaryIdAndUserId(Long gatherDiaryId,Long userId);
}
