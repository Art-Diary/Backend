package klieme.artdiary.favoriteexh.data_access.repository;

import java.util.List;

import com.querydsl.core.Tuple;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;

public interface FavoriteExhRepoCustom {
	List<Tuple> getFavoriteExhWithUserAndExh();

	List<ExhEntity> getFavoriteExhByUserId(Long userId);
}
