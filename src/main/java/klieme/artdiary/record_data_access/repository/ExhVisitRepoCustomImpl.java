package klieme.artdiary.record_data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringMateEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.entity.QExhVisitEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExhVisitRepoCustomImpl implements ExhVisitRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getMyVisitedDateListOfExh(Long userId, Long exhId) {
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		BooleanBuilder builder = new BooleanBuilder();
		BooleanBuilder gatherBuilder = new BooleanBuilder();

		gatherBuilder.and(exhVisit.gatherId.isNotNull());
		gatherBuilder.and(gatheringMate.gatheringMateId.userId.eq(userId));
		builder.or(gatherBuilder);
		builder.or(exhVisit.userId.eq(userId));

		List<Tuple> tuples = query.select(exhVisit, gathering)
			.from(exhVisit)
			.leftJoin(gatheringMate).on(exhVisit.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
			.leftJoin(gathering).on(gatheringMate.gatheringMateId.gatherId.eq(gathering.gatherId))
			.fetchJoin()
			.where(exhVisit.exhId.eq(exhId), builder)
			.orderBy(exhVisit.gatherId.asc(), exhVisit.visitDate.asc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhVisit", tuple.get(0, ExhVisitEntity.class));
			row.put("gathering", tuple.get(1, GatheringEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public Boolean checkExhVisitByExhVisitId(Long exhVisitId, Long userId, Long exhId) {
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		BooleanBuilder builder = new BooleanBuilder();
		BooleanBuilder gatherBuilder = new BooleanBuilder();

		gatherBuilder.and(exhVisit.gatherId.isNotNull());
		gatherBuilder.and(gatheringMate.gatheringMateId.userId.eq(userId));
		builder.or(gatherBuilder);
		builder.or(exhVisit.userId.eq(userId));

		return query.select(exhVisit, gathering)
			.from(exhVisit)
			.leftJoin(gatheringMate).on(exhVisit.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
			.leftJoin(gathering).on(gatheringMate.gatheringMateId.gatherId.eq(gathering.gatherId))
			.fetchJoin()
			.where(exhVisit.exhVisitId.eq(exhVisitId), exhVisit.exhId.eq(exhId), builder)
			.fetchFirst() != null;
	}
}
