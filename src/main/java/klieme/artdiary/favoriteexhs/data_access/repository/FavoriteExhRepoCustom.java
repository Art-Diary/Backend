package klieme.artdiary.favoriteexhs.data_access.repository;

import java.util.List;

import com.querydsl.core.Tuple;

public interface FavoriteExhRepoCustom {
	List<Tuple> getFavoriteExhWithUserAndExh();
}
