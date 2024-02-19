package klieme.artdiary.mate.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.mate.data_access.entity.MateEntity;

@Repository
public interface MateRepository extends JpaRepository<MateEntity, Long> {
}
