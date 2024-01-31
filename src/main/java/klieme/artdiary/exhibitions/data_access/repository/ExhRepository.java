package klieme.artdiary.exhibitions.data_access.repository;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.users.data_access.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhRepository extends JpaRepository<ExhEntity, Long> {
}
