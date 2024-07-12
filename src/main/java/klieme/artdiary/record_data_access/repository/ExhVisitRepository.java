package klieme.artdiary.record_data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.record_data_access.entity.ExhVisitEntity;

@Repository
public interface ExhVisitRepository extends JpaRepository<ExhVisitEntity, Long>, ExhVisitRepoCustom {
}
