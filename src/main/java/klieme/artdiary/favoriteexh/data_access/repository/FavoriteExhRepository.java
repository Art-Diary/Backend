package klieme.artdiary.favoriteexh.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhId;

@Repository
public interface FavoriteExhRepository extends JpaRepository<FavoriteExhEntity, FavoriteExhId>, FavoriteExhRepoCustom {
	Optional<FavoriteExhEntity> findByFavoriteExhId(FavoriteExhId favoriteExhId);
}
