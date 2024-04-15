package klieme.artdiary.mates.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.mates.data_access.entity.MateEntity;

@Repository
public interface MateRepository extends JpaRepository<MateEntity, Long> {
	Optional<MateEntity> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	List<MateEntity> findByFromUserId(Long fromUserId);
}
