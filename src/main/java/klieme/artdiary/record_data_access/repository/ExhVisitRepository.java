package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.record_data_access.entity.ExhVisitEntity;

@Repository
public interface ExhVisitRepository extends JpaRepository<ExhVisitEntity, Long>, ExhVisitRepoCustom {
	List<ExhVisitEntity> findByUserIdAndExhId(Long userId, Long exhId);

	Optional<ExhVisitEntity> findByUserIdAndExhIdAndVisitDate(Long userId, Long exhId, LocalDate visitDate);

	Optional<ExhVisitEntity> findByGatherIdAndExhIdAndVisitDate(Long gatherId, Long exhId, LocalDate visitDate);

	List<ExhVisitEntity> findByUserId(Long userId);
}
