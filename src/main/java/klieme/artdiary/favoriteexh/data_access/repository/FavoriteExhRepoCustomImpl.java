package klieme.artdiary.favoriteexh.data_access.repository;

import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.QFavoriteExhEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FavoriteExhRepoCustomImpl implements FavoriteExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Tuple> getFavoriteExhWithUserAndExh() {
		QFavoriteExhEntity favoriteExh = QFavoriteExhEntity.favoriteExhEntity;
		QUserEntity user = QUserEntity.userEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		return query
			.select(user, exh)
			.from(favoriteExh)
			.leftJoin(user).on(favoriteExh.favoriteExhId.userId.eq(user.userId))
			.leftJoin(exh).on(favoriteExh.favoriteExhId.exhId.eq(exh.exhId))
			.fetchJoin()
			.fetch();
	}
}
