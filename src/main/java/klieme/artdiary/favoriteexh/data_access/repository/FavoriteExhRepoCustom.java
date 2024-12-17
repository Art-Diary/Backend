package klieme.artdiary.favoriteexh.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;

public interface FavoriteExhRepoCustom {
	List<Map<String, Object>> getFavoriteExhWithUserAndExh();

	List<ExhEntity> getFavoriteExhByUserId(Long userId);
}
