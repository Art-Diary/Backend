package klieme.artdiary.exhibitions.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;

@Repository
public interface UserExhRepository extends JpaRepository<UserExhEntity, Long> {
	List<UserExhEntity> findByUserIdAndExhId(Long userId, Long exhId);

}