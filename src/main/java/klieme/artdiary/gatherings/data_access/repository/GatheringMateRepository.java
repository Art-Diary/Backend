package klieme.artdiary.gatherings.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;

@Repository
public interface GatheringMateRepository extends JpaRepository<GatheringMateEntity, GatheringMateId> {
	Optional<GatheringMateEntity> findByGatheringMateId(GatheringMateId gatheringMateId);
}
