package klieme.artdiary.exhibition.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QRegExhEntity;
import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegExhCustomImpl implements RegExhCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getRegExhListByAdmin() {
		QRegExhEntity regExh = QRegExhEntity.regExhEntity;
		QUserEntity user = QUserEntity.userEntity;

		List<Tuple> tuples = query
			.select(regExh, user)
			.from(regExh)
			.leftJoin(user).on(regExh.userId.eq(user.userId))
			.fetchJoin()
			.orderBy(new CaseBuilder()
					.when(regExh.regState.eq("대기")).then(1)
					.when(regExh.regState.eq("완료")).then(2)
					.when(regExh.regState.eq("실패")).then(3)
					.otherwise(4)
					.asc()
				, regExh.regDate.desc())
			.fetch();

		List<Map<String, Object>> results = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("regExhEntity", tuple.get(0, RegExhEntity.class));
			row.put("userEntity", tuple.get(1, UserEntity.class));
			results.add(row);
		}
		return results;
	}

	@Override
	public List<RegExhEntity> getRegExhListByUser(Long userId) {
		QRegExhEntity regExh = QRegExhEntity.regExhEntity;

		return query
			.select(regExh)
			.from(regExh)
			.where(regExh.userId.eq(userId))
			.orderBy(new CaseBuilder()
					.when(regExh.regState.eq("대기")).then(1)
					.when(regExh.regState.eq("완료")).then(2)
					.when(regExh.regState.eq("실패")).then(3)
					.otherwise(4)
					.asc() // 우선순위를 오름차순으로 정렬
				, regExh.regDate.desc())
			.fetch();
	}

	@Override
	public Map<String, Object> getRegExhWithExhByAdmin(Long regExhId) {
		QRegExhEntity regExh = QRegExhEntity.regExhEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		Tuple tuple = query
			.select(regExh, exh)
			.from(regExh)
			.leftJoin(exh).on(regExh.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(regExh.regExhId.eq(regExhId))
			.fetchFirst();

		Map<String, Object> result = new HashMap<>();

		if (tuple != null) {
			result.put("regExhEntity", tuple.get(0, RegExhEntity.class));
			result.put("exhEntity", tuple.get(1, ExhEntity.class));
		}
		return result;
	}
}
