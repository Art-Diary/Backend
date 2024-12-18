package klieme.artdiary.gathering.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringMateEntity;
import klieme.artdiary.mate.data_access.entity.QMateEntity;
import klieme.artdiary.record_data_access.entity.QExhVisitEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GatheringMateRepoCustomImpl implements GatheringMateRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<GatheringEntity> getGatheringListByRecentVisitDate(Long userId) {
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;

		// 사용자가 모임에서 최근에 전시회를 방문한 날짜 순으로 정렬
		return query
			.select(gathering)
			.from(gatheringMate)
			.leftJoin(exhVisit).on(gatheringMate.gatheringMateId.gatherId.eq(exhVisit.gatherId))
			.leftJoin(gathering).on(gatheringMate.gatheringMateId.gatherId.eq(gathering.gatherId))
			.fetchJoin()
			.where(gatheringMate.gatheringMateId.userId.eq(userId))
			.groupBy(gatheringMate.gatheringMateId.gatherId)
			.orderBy(exhVisit.visitDate.max().desc())
			.fetch();
	}

	@Override
	public List<Map<String, Object>> getGatheringMateListForSearch(Long gatherId, Long userId, String nickname) {
		QMateEntity mate = QMateEntity.mateEntity;
		QUserEntity user = QUserEntity.userEntity;
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;

		List<Tuple> tuples = query
			.select(user, new CaseBuilder()
				.when(gatheringMate.gatheringMateId.gatherId.isNull()).then(false)
				.otherwise(true))
			.from(mate)
			.leftJoin(gatheringMate)
			.on(mate.toUserId.eq(gatheringMate.gatheringMateId.userId),
				gatheringMate.gatheringMateId.gatherId.eq(gatherId))
			.leftJoin(user)
			.on(mate.toUserId.eq(user.userId))
			.fetchJoin()
			.where(mate.fromUserId.eq(userId)
				, user.nickname.toLowerCase().notLike("%kakao_%")
				, user.nickname.toLowerCase().notLike("%google_%")
				, user.nickname.toLowerCase().notLike("%naver_%")
				, user.nickname.contains(nickname))
			.orderBy(user.nickname.asc())
			.fetch();

		List<Map<String, Object>> results = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("userEntity", tuple.get(0, UserEntity.class));
			row.put("isGatheringMate", tuple.get(1, Boolean.class));
			results.add(row);
		}
		return results;
	}

	@Override
	public List<Map<String, Object>> getGatheringMateList(Long gatherId) {
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QUserEntity user = QUserEntity.userEntity;

		// 사용자가 모임에서 최근에 전시회를 방문한 날짜 순으로 정렬
		List<Tuple> tuples = query
			.select(user, gathering)
			.from(gatheringMate)
			.leftJoin(gathering).on(gatheringMate.gatheringMateId.gatherId.eq(gathering.gatherId))
			.leftJoin(user).on(gatheringMate.gatheringMateId.userId.eq(user.userId))
			.fetchJoin()
			.where(gatheringMate.gatheringMateId.gatherId.eq(gatherId))
			.fetch();

		List<Map<String, Object>> results = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("user", tuple.get(0, UserEntity.class));
			row.put("gathering", tuple.get(1, GatheringEntity.class));
			results.add(row);
		}
		return results;
	}
}
