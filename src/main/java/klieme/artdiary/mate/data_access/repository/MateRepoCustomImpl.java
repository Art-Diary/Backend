package klieme.artdiary.mate.data_access.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.mate.data_access.entity.QMateEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MateRepoCustomImpl implements MateRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<UserEntity> getMyMateListByFromUserId(Long fromUserId) {
		QMateEntity mate = QMateEntity.mateEntity;
		QUserEntity user = QUserEntity.userEntity;

		return query
			.select(user)
			.from(mate)
			.leftJoin(user).on(mate.toUserId.eq(user.userId))
			.fetchJoin()
			.where(mate.fromUserId.eq(fromUserId))
			.orderBy(user.nickname.asc())
			.fetch();
	}
}
