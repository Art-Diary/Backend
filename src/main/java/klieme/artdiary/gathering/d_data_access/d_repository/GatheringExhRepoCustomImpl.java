// package klieme.artdiary.gathering.data_access.repository;
//
// import java.util.List;
//
// import com.querydsl.core.Tuple;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
// import klieme.artdiary.gathering.data_access.entity.QGatheringExhEntity;
// import klieme.artdiary.gathering.data_access.entity.QGatheringMateEntity;
// import klieme.artdiary.user.data_access.entity.QUserEntity;
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// public class GatheringExhRepoCustomImpl implements GatheringExhRepoCustom {
// 	private final JPAQueryFactory query;
//
// 	@Override
// 	public List<Tuple> getVisitExhWithUser() {
// 		QUserEntity user = QUserEntity.userEntity;
// 		QExhEntity exh = QExhEntity.exhEntity;
// 		QGatheringMateEntity gatheringMate = QGatheringMateEntity.gatheringMateEntity;
// 		QGatheringExhEntity gatheringExh = QGatheringExhEntity.gatheringExhEntity;
//
// 		return query
// 			.select(user, exh, gatheringExh)
// 			.from(gatheringExh)
// 			.leftJoin(gatheringMate).on(gatheringExh.gatherId.eq(gatheringMate.gatheringMateId.gatherId))
// 			.leftJoin(user).on(gatheringMate.gatheringMateId.userId.eq(user.userId))
// 			.leftJoin(exh).on(gatheringExh.exhId.eq(exh.exhId))
// 			.fetchJoin()
// 			.fetch();
// 	}
// }
