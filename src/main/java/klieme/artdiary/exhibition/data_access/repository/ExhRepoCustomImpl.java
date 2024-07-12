package klieme.artdiary.exhibition.data_access.repository;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExhRepoCustomImpl implements ExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<ExhEntity> searchExhList(String searchName, List<ExhField> fieldList, ExhPrice price,
		List<ExhState> stateList,
		LocalDate date) {
		QExhEntity exh = QExhEntity.exhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (searchName != null) {
			builder.and(exh.exhName.containsIgnoreCase(searchName).or(exh.gallery.containsIgnoreCase(searchName)));
		}
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
		if (stateList != null || date != null) {
			LocalDate now = date != null ? date : LocalDate.now();

			if (date != null) {
				builder.and(exh.exhPeriodStart.loe(now)); // start <= now
				builder.and(exh.exhPeriodEnd.goe(now)); // end >= now
			} else {
				BooleanBuilder stateListBuilder = new BooleanBuilder();

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
		return query.selectFrom(exh)
			.where(builder)
			.fetch();
	}
}
