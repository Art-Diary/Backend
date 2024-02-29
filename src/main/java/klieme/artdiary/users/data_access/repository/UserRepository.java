package klieme.artdiary.users.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.users.data_access.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUserId(Long userId);

	Optional<UserEntity> findByUserIdAndNicknameContainingIgnoreCase(Long userId, String nickname);

	List<UserEntity> findByNicknameContainingIgnoreCase(String nickname);

}
