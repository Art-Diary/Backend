package klieme.artdiary.favoriteexh.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.QFavoriteExhEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FavoriteExhRepoCustomImpl implements FavoriteExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getFavoriteExhWithUserAndExh() {
		QFavoriteExhEntity favoriteExh = QFavoriteExhEntity.favoriteExhEntity;
		QUserEntity user = QUserEntity.userEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(user, exh)
			.from(favoriteExh)
			.leftJoin(user).on(favoriteExh.favoriteExhId.userId.eq(user.userId))
			.leftJoin(exh).on(favoriteExh.favoriteExhId.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(user.alarmToken.isNotNull(), exh.exhId.isNotNull(), user.favoriteExhAlarm.eq(true))
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("user", tuple.get(0, UserEntity.class));
			row.put("exhibition", tuple.get(1, ExhEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<ExhEntity> getFavoriteExhByUserId(Long userId) {
		QFavoriteExhEntity favoriteExh = QFavoriteExhEntity.favoriteExhEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		return query
			.select(exh)
			.from(favoriteExh)
			.leftJoin(exh).on(favoriteExh.favoriteExhId.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(favoriteExh.favoriteExhId.userId.eq(userId))
			.orderBy(favoriteExh.initDate.desc(), exh.exhName.asc())
			.fetch();
	}
}
