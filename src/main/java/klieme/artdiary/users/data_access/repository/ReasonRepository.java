package klieme.artdiary.users.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.users.data_access.entity.ReasonEntity;

@Repository
public interface ReasonRepository extends JpaRepository<ReasonEntity, Long> {
}
