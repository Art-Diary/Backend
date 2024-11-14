// package klieme.artdiary.solo.data_access.repository;
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
// import klieme.artdiary.solo.data_access.entity.QMydiaryEntity;
// import klieme.artdiary.solo.data_access.entity.QUserExhEntity;
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// public class MydiaryRepoCustomImpl implements MydiaryRepoCustom {
// 	private final JPAQueryFactory query;
//
// 	@Override
// 	public List<Map<String, Object>> sumRateByUserExhId(Long userId, Boolean withMate) {
// 		QMydiaryEntity myDiary = QMydiaryEntity.mydiaryEntity;
// 		QUserExhEntity userExh = QUserExhEntity.userExhEntity;
// 		QExhEntity exh = QExhEntity.exhEntity;
// 		BooleanBuilder builder = new BooleanBuilder();
//
// 		if (withMate) {
// 			builder.and(myDiary.diaryPrivate.eq(true));
// 		}
//
// 		List<Tuple> tuples = query
// 			.select(myDiary.rate.sum(), myDiary.count(), exh)
// 			.from(myDiary)
// 			.leftJoin(userExh).on(myDiary.userExhId.eq(userExh.userExhId))
// 			.leftJoin(exh).on(userExh.exhId.eq(exh.exhId))
// 			.fetchJoin()
// 			.where(userExh.userId.eq(userId), builder)
// 			.groupBy(userExh.exhId)
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
// 	public List<Tuple> getMyDiaryListInSoloWithJoin(Long userId, Long exhId, Boolean isMate) {
// 		QMydiaryEntity myDiary = QMydiaryEntity.mydiaryEntity;
// 		QUserExhEntity userExh = QUserExhEntity.userExhEntity;
// 		BooleanBuilder builder = new BooleanBuilder();
//
// 		if (isMate) {
// 			builder.and(myDiary.diaryPrivate.eq(true));
// 		}
//
// 		return query
// 			.select(userExh, myDiary)
// 			.from(userExh)
// 			.leftJoin(myDiary).on(myDiary.userExhId.eq(userExh.userExhId))
// 			.fetchJoin()
// 			.where(userExh.userId.eq(userId), userExh.exhId.eq(exhId), builder)
// 			.fetch();
// 	}
//
// 	@Override
// 	public List<Tuple> getMyDiaryListWithDateInSoloWithJoin(Long userId, Long exhId, LocalDate visitDate) {
// 		QMydiaryEntity myDiary = QMydiaryEntity.mydiaryEntity;
// 		QUserExhEntity userExh = QUserExhEntity.userExhEntity;
//
// 		return query
// 			.select(userExh, myDiary)
// 			.from(userExh)
// 			.leftJoin(myDiary).on(myDiary.userExhId.eq(userExh.userExhId))
// 			.fetchJoin()
// 			.where(userExh.userId.eq(userId), userExh.exhId.eq(exhId),
// 				visitDate == null ? userExh.visitDate.isNull() : userExh.visitDate.eq(visitDate))
// 			.fetch();
// 	}
// }
