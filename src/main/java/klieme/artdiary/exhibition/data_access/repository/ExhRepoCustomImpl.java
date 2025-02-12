package klieme.artdiary.exhibition.data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QCategoryEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhCategoryLinkEntity;
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
		QCategoryEntity category = QCategoryEntity.categoryEntity;
		QExhCategoryLinkEntity exhCategoryLinkEntity = QExhCategoryLinkEntity.exhCategoryLinkEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (fieldList != null) {
			BooleanBuilder fieldBuilder = new BooleanBuilder();

			for (ExhField field : fieldList) {
				if (field == ExhField.OTHER) { // 그 외일 경우 처리
					fieldBuilder.or(category.name.isNull());
				} else {
					fieldBuilder.or(category.name.eq(field.label()));
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

		if ((fieldList == null || fieldList.isEmpty()) && price == null && (stateList == null || stateList.isEmpty())
			&& date == null) { //아무 조건도 없을 때

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
			.distinct()
			.from(exh)
			.leftJoin(favoriteExh).on(exh.exhId.eq(favoriteExh.favoriteExhId.exhId))
			.leftJoin(exhCategoryLinkEntity).on(exh.exhId.eq(exhCategoryLinkEntity.exhCategoryLinkId.exhId))
			.leftJoin(category).on(exhCategoryLinkEntity.exhCategoryLinkId.categoryId.eq(category.categoryId))
			.fetchJoin()
			.where(builder)
			.groupBy(exh.exhId)
			.orderBy(favoriteExh.favoriteExhId.exhId.count().desc(), exh.exhName.asc())
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

		OrderSpecifier<Integer> priorityOrder = new CaseBuilder()
			.when(exh.exhName.containsIgnoreCase(searchName)).then(1) // 1순위: exhName
			.when(exh.gallery.containsIgnoreCase(searchName)).then(2) // 2순위: gallery
			.when(exh.painter.containsIgnoreCase(searchName)).then(3) // 3순위: painter
			.otherwise(4) // 나머지
			.asc();

		// 필드별 정렬 조건
		OrderSpecifier<String> exhNameOrder = new CaseBuilder()
			.when(exh.exhName.containsIgnoreCase(searchName)).then(exh.exhName)
			.otherwise((String)null) // 다른 조건일 때는 무시
			.asc();

		OrderSpecifier<String> galleryOrder = new CaseBuilder()
			.when(exh.gallery.containsIgnoreCase(searchName)).then(exh.gallery)
			.otherwise((String)null)
			.asc();

		OrderSpecifier<String> painterOrder = new CaseBuilder()
			.when(exh.painter.containsIgnoreCase(searchName)).then(exh.painter)
			.otherwise((String)null)
			.asc();

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
			.orderBy(priorityOrder, exhNameOrder, galleryOrder, painterOrder)
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
	public List<Map<String, Object>> getExhListForExhData() {
		QExhEntity exh = QExhEntity.exhEntity;
		QCategoryEntity category = QCategoryEntity.categoryEntity;
		QExhCategoryLinkEntity exhCategoryLinkEntity = QExhCategoryLinkEntity.exhCategoryLinkEntity;

		List<Tuple> tuples = query.select(exh, Expressions.stringTemplate("GROUP_CONCAT({0})", category.name))
			.distinct()
			.from(exh)
			.leftJoin(exhCategoryLinkEntity).on(exh.exhId.eq(exhCategoryLinkEntity.exhCategoryLinkId.exhId))
			.leftJoin(category).on(exhCategoryLinkEntity.exhCategoryLinkId.categoryId.eq(category.categoryId))
			.fetchJoin()
			.groupBy(exh.exhId)
			.orderBy(exh.exhId.desc())
			.fetch();
		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("category", tuple.get(1, String.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public Map<String, Object> getExhDetailInfo(Long userId, Long exhId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QFavoriteExhEntity favoriteExh = QFavoriteExhEntity.favoriteExhEntity;
		QCategoryEntity category = QCategoryEntity.categoryEntity;
		QExhCategoryLinkEntity exhCategoryLinkEntity = QExhCategoryLinkEntity.exhCategoryLinkEntity;

		Tuple tuple = query.select(exh, Expressions.stringTemplate("GROUP_CONCAT({0})", category.name),
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
			.leftJoin(favoriteExh).on(exh.exhId.eq(favoriteExh.favoriteExhId.exhId))
			.leftJoin(exhCategoryLinkEntity).on(exh.exhId.eq(exhCategoryLinkEntity.exhCategoryLinkId.exhId))
			.leftJoin(category).on(exhCategoryLinkEntity.exhCategoryLinkId.categoryId.eq(category.categoryId))
			.fetchJoin()
			.where(exh.exhId.eq(exhId))
			.groupBy(exh.exhId)
			.orderBy(exh.exhId.desc())
			.fetchOne();
		if (tuple != null) {
			Map<String, Object> result = new HashMap<>();

			result.put("exhibition", tuple.get(0, ExhEntity.class));
			result.put("category", tuple.get(1, String.class));
			return result;
		}
		return null;
	}
}
