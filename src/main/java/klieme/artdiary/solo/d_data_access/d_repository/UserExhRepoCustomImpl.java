// package klieme.artdiary.solo.data_access.repository;
//
// import java.util.List;
//
// import com.querydsl.core.Tuple;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
// import klieme.artdiary.solo.data_access.entity.QUserExhEntity;
// import klieme.artdiary.user.data_access.entity.QUserEntity;
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// public class UserExhRepoCustomImpl implements UserExhRepoCustom {
// 	private final JPAQueryFactory query;
//
// 	@Override
// 	public List<Tuple> getVisitExhWithUser() {
// 		QUserEntity user = QUserEntity.userEntity;
// 		QExhEntity exh = QExhEntity.exhEntity;
// 		QUserExhEntity userExh = QUserExhEntity.userExhEntity;
//
// 		return query
// 			.select(user, exh, userExh)
// 			.from(userExh)
// 			.leftJoin(user).on(userExh.userId.eq(user.userId))
// 			.leftJoin(exh).on(userExh.exhId.eq(exh.exhId))
// 			.fetchJoin()
// 			.fetch();
// 	}
//
// 	// @Override
// 	// public List<Tuple> findByUserIdAndVisitDateBetween(Long userId, LocalDate visitDateStart,
// 	// 	LocalDate visitDateEnd) {
// 	// 	QUserExhEntity userExh = QUserExhEntity.userExhEntity;
// 	// 	QExhEntity exh = QExhEntity.exhEntity;
// 	//
// 	// 	return query
// 	// 		.select(userExh, exh)
// 	// 		.from(userExh)
// 	// 		.leftJoin(exh).on(userExh.exhId.eq(exh.exhId))
// 	// 		.fetchJoin()
// 	// 		.where(userExh.visitDate.between(visitDateStart, visitDateEnd))
// 	// 		.distinct()
// 	// 		.fetch();
// 	// }
// }
