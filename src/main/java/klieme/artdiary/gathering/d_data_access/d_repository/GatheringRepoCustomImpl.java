// package klieme.artdiary.gathering.data_access.repository;
//
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import com.querydsl.core.BooleanBuilder;
// import com.querydsl.core.Tuple;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
// import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
// import klieme.artdiary.gathering.data_access.entity.QGatheringDiaryEntity;
// import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
// import klieme.artdiary.gathering.data_access.entity.QGatheringExhEntity;
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// public class GatheringRepoCustomImpl implements GatheringRepoCustom {
// 	private final JPAQueryFactory query;
//
// 	@Override
// 	public List<Map<String, Object>> sumRateByGatherExhId(Long userId, Boolean withMate) {
// 		QGatheringDiaryEntity gatheringDiary = QGatheringDiaryEntity.gatheringDiaryEntity;
// 		QGatheringExhEntity gatheringExh = QGatheringExhEntity.gatheringExhEntity;
// 		QExhEntity exh = QExhEntity.exhEntity;
// 		BooleanBuilder builder = new BooleanBuilder();
//
// 		if (withMate) {
// 			builder.and(gatheringDiary.diaryPrivate.eq(true));
// 		}
//
// 		List<Tuple> tuples = query
// 			.select(gatheringDiary.rate.sum(), gatheringDiary.count(), exh)
// 			.from(gatheringDiary)
// 			.leftJoin(gatheringExh).on(gatheringDiary.gatherExhId.eq(gatheringExh.gatherExhId))
// 			.leftJoin(exh).on(gatheringExh.exhId.eq(exh.exhId))
// 			.fetchJoin()
// 			.where(gatheringDiary.userId.eq(userId), builder)
// 			.groupBy(gatheringExh.exhId)
// 			.fetch();
//
// 		List<Map<String, Object>> result = new ArrayList<>();
//
// 		for (Tuple tuple : tuples) {
// 			Map<String, Object> row = new HashMap<>();
// 			row.put("sumOfRate", tuple.get(0, Long.class));
// 			row.put("count", tuple.get(1, Long.class));
// 			row.put("exhibition", tuple.get(2, ExhEntity.class));
// 			result.add(row);
// 		}
// 		return result;
// 	}
//
// 	@Override
// 	public List<Tuple> getMyDiaryListInGatheringWithJoin(Long userId, Long exhId, Boolean isMate) {
// 		QGatheringDiaryEntity gatheringDiary = QGatheringDiaryEntity.gatheringDiaryEntity;
// 		QGatheringExhEntity gatheringExh = QGatheringExhEntity.gatheringExhEntity;
// 		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
// 		BooleanBuilder builder = new BooleanBuilder();
//
// 		if (isMate) {
// 			builder.and(gatheringDiary.diaryPrivate.eq(true));
// 		}
//
// 		return query
// 			.select(gathering, gatheringExh, gatheringDiary)
// 			.from(gatheringDiary)
// 			.leftJoin(gatheringExh).on(gatheringExh.gatherExhId.eq(gatheringDiary.gatherExhId))
// 			.leftJoin(gathering).on(gathering.gatherId.eq(gatheringExh.gatherId))
// 			.fetchJoin()
// 			.where(gatheringDiary.userId.eq(userId), gatheringExh.exhId.eq(exhId), builder)
// 			.fetch();
// 	}
//
// 	@Override
// 	public List<Tuple> getMyDiaryListWithDateInGatheringWithJoin(Long userId, Long exhId, LocalDate visitDate,
// 		Long gatherId) {
// 		QGatheringDiaryEntity gatheringDiary = QGatheringDiaryEntity.gatheringDiaryEntity;
// 		QGatheringExhEntity gatheringExh = QGatheringExhEntity.gatheringExhEntity;
// 		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
//
// 		return query
// 			.select(gathering, gatheringExh, gatheringDiary)
// 			.from(gatheringDiary)
// 			.leftJoin(gatheringExh).on(gatheringExh.gatherExhId.eq(gatheringDiary.gatherExhId))
// 			.leftJoin(gathering).on(gathering.gatherId.eq(gatheringExh.gatherId))
// 			.fetchJoin()
// 			.where(gatheringDiary.userId.eq(userId), gatheringExh.exhId.eq(exhId),
// 				visitDate == null ? gatheringExh.visitDate.isNull() : gatheringExh.visitDate.eq(visitDate),
// 				gatheringExh.gatherId.eq(gatherId))
// 			.fetch();
// 	}
// }
