package klieme.artdiary.gathering.data_access.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringMateEntity;
import klieme.artdiary.record_data_access.entity.QExhVisitEntity;
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
			.groupBy(exhVisit.gatherId)
			.orderBy(exhVisit.visitDate.max().desc())
			.fetch();
	}
}
