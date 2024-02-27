package klieme.artdiary.gatherings.data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;

@Repository
public interface GatheringExhRepository extends JpaRepository<GatheringExhEntity, Long> {
	Optional<GatheringExhEntity> findByGatherIdAndExhIdAndVisitDate(Long gatherId, Long exhId, LocalDate visitDate);

	List<GatheringExhEntity> findByGatherId(Long gatherId);

	List<GatheringExhEntity> findByExhId(Long exhId);

	List<GatheringExhEntity> findByGatherIdAndExhId(Long gatherId, Long exhId);

	Optional<GatheringExhEntity> findByGatheringExhId(Long gatheringExhId);

	List<GatheringExhEntity> findByGatherIdAndVisitDate(Long gatherId, LocalDate visitDate);
}
