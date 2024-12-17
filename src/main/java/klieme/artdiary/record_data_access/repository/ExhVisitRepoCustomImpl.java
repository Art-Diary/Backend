package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringMateEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.entity.QExhVisitEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
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
	public List<ExhVisitEntity> getGroupVisitedDateListOfExh(Long userId, Long groupId, Long exhId) {
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;

		List<ExhVisitEntity> entities = query.select(exhVisit)
			.from(exhVisit)
			.leftJoin(gatheringMate).on(exhVisit.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
			.fetchJoin()
			.where(exhVisit.exhId.eq(exhId), exhVisit.gatherId.eq(groupId),
				gatheringMate.gatheringMateId.userId.eq(userId))
			.orderBy(exhVisit.gatherId.asc(), exhVisit.visitDate.asc())
			.fetch();

		return entities;
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

	@Override
	public List<Map<String, Object>> getVisitInfoForCalendar(CalendarKind kind, Long userId, Long gatherId,
		LocalDate startDate, LocalDate endDate) {
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QExhEntity exh = QExhEntity.exhEntity;
		BooleanBuilder builder = new BooleanBuilder();
		BooleanBuilder gatherBuilder = new BooleanBuilder();

		if (kind == CalendarKind.ALONE) { // 개인일 경우
			builder.and(exhVisit.userId.eq(userId));
		} else if (kind == CalendarKind.GATHER) { // 모임일 경우
			builder.and(exhVisit.gatherId.eq(gatherId));
			builder.and(gatheringMate.gatheringMateId.userId.eq(userId));
		} else { // 전체일 경우
			gatherBuilder.and(exhVisit.gatherId.isNotNull());
			gatherBuilder.and(gatheringMate.gatheringMateId.userId.eq(userId));
			builder.or(gatherBuilder);
			builder.or(exhVisit.userId.eq(userId));
		}

		List<Tuple> tuples = query.select(exhVisit, gathering, exh)
			.from(exhVisit)
			.leftJoin(gatheringMate).on(exhVisit.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
			.leftJoin(gathering).on(gatheringMate.gatheringMateId.gatherId.eq(gathering.gatherId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(builder, exhVisit.visitDate.goe(startDate), exhVisit.visitDate.loe(endDate))
			.fetch();
		// startDate <= visitDate && visitDate <= endDate

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhVisit", tuple.get(0, ExhVisitEntity.class));
			row.put("gathering", tuple.get(1, GatheringEntity.class));
			row.put("exhibition", tuple.get(2, ExhEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getVisitSoloDateForFcm() {
		QUserEntity user = QUserEntity.userEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(user, exh)
			.from(exhVisit)
			.leftJoin(user).on(exhVisit.userId.eq(user.userId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(exh.exhId.isNotNull(), user.visitSoloAlarm.eq(true), user.alarmToken.isNotNull(),
				exhVisit.visitDate.eq(LocalDate.now()))
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
	public List<Map<String, Object>> getVisitGatheringDateForFcm() {
		QUserEntity user = QUserEntity.userEntity;
		QExhVisitEntity exhVisit = QExhVisitEntity.exhVisitEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(user, exh, gathering)
			.from(exhVisit)
			.leftJoin(gathering).on(exhVisit.gatherId.eq(gathering.gatherId))
			.leftJoin(gatheringMate).on(exhVisit.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
			.leftJoin(user).on(gatheringMate.gatheringMateId.userId.eq(user.userId))
			.leftJoin(exh).on(exhVisit.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(exh.exhId.isNotNull(), user.visitGatheringAlarm.eq(true), user.alarmToken.isNotNull(),
				exhVisit.visitDate.eq(LocalDate.now()))
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("user", tuple.get(0, UserEntity.class));
			row.put("exhibition", tuple.get(1, ExhEntity.class));
			row.put("gathering", tuple.get(2, GatheringEntity.class));
			result.add(row);
		}
		return result;
	}
}
