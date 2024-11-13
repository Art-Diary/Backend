package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.entity.QDiaryEntity;
import klieme.artdiary.record_data_access.entity.QExhVisitEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DiaryRepoCustomImpl implements DiaryRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getMyDiarySumRateAndCount(Long userId, Boolean isMate) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QExhEntity exh = QExhEntity.exhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (isMate) {
			builder.and(diary.diaryPrivate.eq(true));
		}
		List<Tuple> tuples = query
			.select(diary.rate.sum(), diary.count(), exh)
			.from(diary)
			.leftJoin(exhVisit).on(diary.exhVisitId.eq(exhVisit.exhVisitId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(diary.writerId.eq(userId), builder)
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

	@Override
	public List<Map<String, Object>> getGatherDiarySumRateAndCount(Long userId, Long gatherId) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QExhEntity exh = QExhEntity.exhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		builder.or(diary.diaryId.isNull());
		builder.or(diary.diaryPrivate.eq(true));
		builder.or(diary.writerId.eq(userId));

		List<Tuple> tuples = query
			.select(diary.rate.sum(), diary.count(), exh)
			.from(exhVisit)
			.leftJoin(diary).on(exhVisit.exhVisitId.eq(diary.exhVisitId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(exhVisit.gatherId.eq(gatherId), builder)
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

	@Override
	public List<Map<String, Object>> getDiaryList(Long userId, Long exhId, Boolean isSolo, Long gatherId,
		Boolean isForget, LocalDate visitDate, Boolean isMate) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (isMate) {
			builder.and(diary.diaryPrivate.eq(true));
		}
		if (isSolo != null) {
			if (isSolo) {
				builder.and(exhVisit.userId.eq(userId));
			} else {
				builder.and(exhVisit.gatherId.eq(gatherId));
			}
		}
		if (isForget) {
			builder.and(exhVisit.visitDate.isNull());
		}
		if (visitDate != null) {
			builder.and(exhVisit.visitDate.eq(visitDate));
		}

		List<Tuple> tuples = query
			.select(diary, exhVisit, gathering)
			.from(diary)
			.leftJoin(exhVisit).on(diary.exhVisitId.eq(exhVisit.exhVisitId))
			.leftJoin(gathering).on(exhVisit.gatherId.eq(gathering.gatherId))
			.fetchJoin()
			.where(diary.writerId.eq(userId), exhVisit.exhId.eq(exhId), builder)
			.orderBy(diary.initDate.desc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("diaryEntity", tuple.get(0, DiaryEntity.class));
			row.put("exhVisitEntity", tuple.get(1, ExhVisitEntity.class));
			row.put("gatheringEntity", tuple.get(2, GatheringEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getAllOfDiaries(Long userId, Long exhId) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QExhEntity exh = QExhEntity.exhEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QUserEntity user = QUserEntity.userEntity;
		BooleanBuilder builder = new BooleanBuilder();
		BooleanBuilder privateBuilder = new BooleanBuilder();

		builder.and(diary.diaryPrivate.eq(false));
		builder.and(diary.writerId.eq(userId));
		privateBuilder.or(builder);
		privateBuilder.or(diary.diaryPrivate.eq(true));

		List<Tuple> tuples = query
			.select(diary, exhVisit, gathering, user, exh)
			.from(diary)
			.leftJoin(exhVisit).on(diary.exhVisitId.eq(exhVisit.exhVisitId))
			.leftJoin(gathering).on(exhVisit.gatherId.eq(gathering.gatherId))
			.leftJoin(user).on(diary.writerId.eq(user.userId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(exhVisit.exhId.eq(exhId), privateBuilder)
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("diaryEntity", tuple.get(0, DiaryEntity.class));
			row.put("exhVisitEntity", tuple.get(1, ExhVisitEntity.class));
			row.put("gatheringEntity", tuple.get(2, GatheringEntity.class));
			row.put("userEntity", tuple.get(3, UserEntity.class));
			row.put("exhEntity", tuple.get(4, ExhEntity.class));

			result.add(row);
		}
		return result;

	}

	@Override
	public DiaryEntity getDiaryByDiaryIdAndWriterIdAndExhId(Long diaryId, Long writerId, Long exhId) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;

		return query
			.select(diary)
			.from(diary)
			.leftJoin(exhVisit).on(diary.exhVisitId.eq(exhVisit.exhVisitId))
			.fetchJoin()
			.where(diary.diaryId.eq(diaryId), diary.writerId.eq(writerId), exhVisit.exhId.eq(exhId))
			.fetchFirst();
	}

	@Override
	public List<Map<String, Object>> getGatherDiaryList(Long gatherId, Long exhId) {
		QDiaryEntity diary = QDiaryEntity.diaryEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QUserEntity user = QUserEntity.userEntity;

		List<Tuple> tuples = query
			.select(diary, exhVisit, gathering, user)
			.from(diary)
			.leftJoin(exhVisit).on(diary.exhVisitId.eq(exhVisit.exhVisitId))
			.leftJoin(gathering).on(exhVisit.gatherId.eq(gathering.gatherId))
			.leftJoin(user).on(diary.writerId.eq(user.userId))
			.fetchJoin()
			.where(exhVisit.exhId.eq(exhId), exhVisit.gatherId.eq(gatherId))
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("diaryEntity", tuple.get(0, DiaryEntity.class));
			row.put("exhVisitEntity", tuple.get(1, ExhVisitEntity.class));
			row.put("gatheringEntity", tuple.get(2, GatheringEntity.class));
			row.put("userEntity", tuple.get(3, UserEntity.class));
			result.add(row);
		}
		return result;
	}
}
