package klieme.artdiary.mate.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.mate.data_access.entity.QMateEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MateRepoCustomImpl implements MateRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<UserEntity> getMyMateListByFromUserId(Long fromUserId) {
		QMateEntity mate = QMateEntity.mateEntity;
		QUserEntity user = QUserEntity.userEntity;

		return query
			.select(user)
			.from(mate)
			.leftJoin(user).on(mate.toUserId.eq(user.userId))
			.fetchJoin()
			.where(mate.fromUserId.eq(fromUserId))
			.orderBy(user.nickname.asc())
			.fetch();
	}

	@Override
	public List<Map<String, Object>> getMateListForSearch(Long fromUserId, String nickname) {
		QMateEntity mate = QMateEntity.mateEntity;
		QUserEntity user = QUserEntity.userEntity;

		List<Tuple> tuples = query
			.select(user, new CaseBuilder()
				.when(mate.mateId.isNull()).then(false)
				.otherwise(true))
			.from(user)
			.leftJoin(mate).on(user.userId.eq(mate.toUserId), mate.fromUserId.eq(fromUserId))
			.fetchJoin()
			.where(user.nickname.toLowerCase().notLike("%kakao_%")
				, user.nickname.toLowerCase().notLike("%google_%")
				, user.nickname.toLowerCase().notLike("%naver_%")
				, user.nickname.contains(nickname))
			.orderBy(user.nickname.asc())
			.fetch();

		List<Map<String, Object>> results = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("userEntity", tuple.get(0, UserEntity.class));
			row.put("isMate", tuple.get(1, Boolean.class));
			results.add(row);
		}
		return results;
	}
}
