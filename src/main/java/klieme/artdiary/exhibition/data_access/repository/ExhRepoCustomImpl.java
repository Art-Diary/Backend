package klieme.artdiary.exhibition.data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import klieme.artdiary.favoriteexh.data_access.entity.QFavoriteExhEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExhRepoCustomImpl implements ExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> searchExhList(String searchName, List<ExhField> fieldList, ExhPrice price,
		List<ExhState> stateList, LocalDate date, Long userId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QFavoriteExhEntity favoriteExh = QFavoriteExhEntity.favoriteExhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (fieldList != null) {
			BooleanBuilder fieldBuilder = new BooleanBuilder();

			for (ExhField field : fieldList) {
				if (field == ExhField.OTHER) { // 그 외일 경우 처리
					fieldBuilder.or(exh.art.isNull());
				} else {
					fieldBuilder.or(exh.art.eq(field.label()));
				}
			}
			builder.and(fieldBuilder);
		}
		if (price != null) {
			if (price == ExhPrice.FREE) {
				builder.and(exh.fee.eq(0));
			} else if (price == ExhPrice.PAY) {
				builder.and(exh.fee.ne(0));
			} else {
				builder.and(exh.fee.loe(20000)); // fee <= 20000
			}
		}
		if ((stateList != null && !stateList.isEmpty()) || date != null) {
			LocalDate now = date != null ? date : LocalDate.now();

			if (date != null) {//날짜가 있을 때
				builder.and(exh.exhPeriodStart.loe(now)); // start <= now
				builder.and(exh.exhPeriodEnd.goe(now)); // end >= now
			} else {
				BooleanBuilder stateListBuilder = new BooleanBuilder();

				//날짜가 없고 state가 있을 때
				for (ExhState state : stateList) {
					if (state == ExhState.PROCEED) {
						BooleanBuilder stateBuilder = new BooleanBuilder();
						stateBuilder.and(exh.exhPeriodStart.loe(now)); // start <= now
						stateBuilder.and(exh.exhPeriodEnd.goe(now)); // end >= now
						stateListBuilder.or(stateBuilder);
					} else if (state == ExhState.BEFORE_START) {
						stateListBuilder.or(exh.exhPeriodStart.gt(now)); // start > now
					} else {
						stateListBuilder.or(exh.exhPeriodEnd.lt(now)); // end < now
					}
				}
				builder.and(stateListBuilder);
			}

		}

		if (fieldList.isEmpty() && price == null && stateList.isEmpty() && date == null) {//아무 조건도 없을 때

			BooleanBuilder stateBuilder = new BooleanBuilder();
			LocalDate now = LocalDate.now();

			stateBuilder.and(exh.exhPeriodStart.loe(now)); // start <= now
			stateBuilder.and(exh.exhPeriodEnd.goe(now)); // end >= now
			builder.and(stateBuilder);

		}
		List<Tuple> tuples = query.select(exh,
				new CaseBuilder()
					.when(
						JPAExpressions.selectOne()
							.from(favoriteExh)
							.where(favoriteExh.favoriteExhId.exhId.eq(exh.exhId)
								.and(favoriteExh.favoriteExhId.userId.eq(userId)))
							.exists()
					).then(1)
					.otherwise(0))
			.from(exh)
			.leftJoin(favoriteExh).on(exh.exhId.eq(favoriteExh.favoriteExhId.exhId))
			.fetchJoin()
			.where(builder)
			.groupBy(exh.exhId)
			.orderBy(favoriteExh.favoriteExhId.exhId.count().desc(), exh.exhName.asc()) // exh.exhPeriodStart.desc(),
			.fetch();
		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("haveFavoriteByUser", tuple.get(1, Boolean.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> searchExhListBySearchName(String searchName, Long userId) {

		QExhEntity exh = QExhEntity.exhEntity;
		QFavoriteExhEntity favoriteExh = QFavoriteExhEntity.favoriteExhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (searchName != null) {
			builder.and(exh.exhName.containsIgnoreCase(searchName)
				.or(exh.gallery.containsIgnoreCase(searchName))
				.or(exh.painter.containsIgnoreCase(searchName)));
		}

		List<Tuple> tuples = query.select(exh,
				new CaseBuilder()
					.when(
						JPAExpressions.selectOne()
							.from(favoriteExh)
							.where(favoriteExh.favoriteExhId.exhId.eq(exh.exhId)
								.and(favoriteExh.favoriteExhId.userId.eq(userId)))
							.exists()
					).then(1)
					.otherwise(0))
			.distinct()
			.from(exh)
			.where(builder)
			.orderBy(new CaseBuilder()
				.when(exh.exhName.containsIgnoreCase(searchName)).then(1) // 1순위: exhName 포함 (이름순)
				.when(exh.gallery.containsIgnoreCase(searchName)).then(2) // 2순위: gallery 포함 (갤러리순)
				.when(exh.painter.containsIgnoreCase(searchName)).then(3) // 3순위: painter 포함 (작가순)
				.otherwise(4)  // 나머지는 마지막 우선순위로 정렬
				.asc(), exh.exhName.asc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("haveFavoriteByUser", tuple.get(1, Boolean.class));
			result.add(row);
		}
		return result;

	}
}
