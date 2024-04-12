package klieme.artdiary.mydiarys.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.QExhEntity;
import klieme.artdiary.gatherings.data_access.entity.QGatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.QGatheringExhEntity;
import klieme.artdiary.mydiarys.data_access.entity.QMydiaryEntity;
import klieme.artdiary.myexhs.data_access.entity.QUserExhEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MydiaryRepoCustomImpl implements MydiaryRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> sumRateByUserExhId(Long userId) {
		QMydiaryEntity myDiary = QMydiaryEntity.mydiaryEntity;
		QUserExhEntity userExh = QUserExhEntity.userExhEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(myDiary.rate.sum(), myDiary.count(), exh)
			.from(myDiary)
			.leftJoin(userExh).on(myDiary.userExhId.eq(userExh.userExhId))
			.leftJoin(exh).on(userExh.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(userExh.userId.eq(userId))
			.groupBy(userExh.exhId)
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("sumOfRate", tuple.get(0, Long.class));
			row.put("count", tuple.get(1, Long.class));
			row.put("exhibition", tuple.get(2, ExhEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> sumRateByGatherExhId(Long userId) {
		QGatheringDiaryEntity gatheringDiary = QGatheringDiaryEntity.gatheringDiaryEntity;
		QGatheringExhEntity gatheringExh = QGatheringExhEntity.gatheringExhEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(gatheringDiary.rate.sum(), gatheringDiary.count(), exh)
			.from(gatheringDiary)
			.leftJoin(gatheringExh).on(gatheringDiary.gatheringExhId.eq(gatheringExh.gatheringExhId))
			.leftJoin(exh).on(gatheringExh.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(gatheringDiary.userId.eq(userId))
			.groupBy(gatheringExh.exhId)
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("sumOfRate", tuple.get(0, Long.class));
			row.put("count", tuple.get(1, Long.class));
			row.put("exhibition", tuple.get(2, ExhEntity.class));
			result.add(row);
		}
		return result;
	}
}
