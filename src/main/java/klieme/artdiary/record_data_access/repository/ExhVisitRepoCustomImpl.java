package klieme.artdiary.record_data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		List<Tuple> tuples = query.select(exhVisit, gathering)
			.from(exhVisit)
			.leftJoin(gatheringMate).on(exhVisit.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
			.leftJoin(gathering).on(gatheringMate.gatheringMateId.gatherId.eq(gathering.gatherId))
			.fetchJoin()
			.where(exhVisit.exhId.eq(exhId), exhVisit.gatherId.isNotNull().or(exhVisit.userId.eq(userId)))
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
}
/**
 * select * from exh_visit as ev left join
 * (select gg.gather_id, user_id, gather_name from gathering_mate as gm left join gathering as gg on gm.gather_id=gg.gather_id) as gg
 * on ev.gather_id=gg.gather_id
 * where (ev.gather_id is not null and gg.user_id=3) or (ev.user_id=3)
 * ;
 *
 * select * from exh_visit as ev left join
 *  gathering_mate as gm on ev.gather_id=gm.gather_id left join gathering as gg on gm.gather_id=gg.gather_id
 * where (ev.gather_id is not null and gm.user_id=3) or (ev.user_id=3)
 * ;
 */