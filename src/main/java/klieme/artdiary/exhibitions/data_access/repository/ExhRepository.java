package klieme.artdiary.exhibitions.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;

@Repository
public interface ExhRepository extends JpaRepository<ExhEntity, Long> {

	Optional<ExhEntity> findByExhId(Long exhId);
}
