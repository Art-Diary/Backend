package klieme.artdiary.gatherings.data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;

@Repository
public interface GatheringMateRepository extends JpaRepository<GatheringMateEntity, GatheringMateId> {

	List<GatheringMateEntity> findByGatheringMateIdUserId(Long userId);
}
