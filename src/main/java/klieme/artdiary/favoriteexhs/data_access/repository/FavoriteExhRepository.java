package klieme.artdiary.favoriteexhs.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.favoriteexhs.data_access.entity.FavoriteExhEntity;
import klieme.artdiary.favoriteexhs.data_access.entity.FavoriteExhId;

@Repository
public interface FavoriteExhRepository extends JpaRepository<FavoriteExhEntity, FavoriteExhId> {
	Optional<FavoriteExhEntity> findByFavoriteExhId(FavoriteExhId favoriteExhId);

	List<FavoriteExhEntity> findByFavoriteExhIdUserId(Long userId);
}
