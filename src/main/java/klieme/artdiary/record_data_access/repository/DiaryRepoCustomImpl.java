package klieme.artdiary.record_data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.record_data_access.entity.QDiaryEntity;
import klieme.artdiary.record_data_access.entity.QExhVisitEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DiaryRepoCustomImpl implements DiaryRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getMyDiarySumRateAndCount(Long userId) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(diary.rate.sum(), diary.count(), exh)
			.from(diary)
			.leftJoin(exhVisit).on(diary.exhVisitId.eq(exhVisit.exhVisitId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(diary.writerId.eq(userId))
			.groupBy(exhVisit.exhId)
			.orderBy(exh.exhId.asc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("sumOfRate", tuple.get(0, Long.class));
			row.put("countOfDiary", tuple.get(1, Long.class));
			row.put("exhibition", tuple.get(2, ExhEntity.class));
			result.add(row);
		}
		return result;
	}
}
