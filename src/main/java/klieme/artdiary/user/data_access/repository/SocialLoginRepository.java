package klieme.artdiary.user.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.user.data_access.entity.SocialLoginEntity;
import klieme.artdiary.user.data_access.entity.SocialLoginId;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLoginEntity, SocialLoginId> {
	Optional<SocialLoginEntity> findByUserId(Long userId);

	Optional<SocialLoginEntity> findBySocialLoginId(SocialLoginId socialLoginId);
}
