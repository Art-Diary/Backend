package klieme.artdiary.gatherings.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;

@Repository
public interface GatheringRepository extends JpaRepository<GatheringEntity, Long> {

	Optional<GatheringEntity> findByGatherId(Long gatherId);
}
