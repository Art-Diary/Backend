package klieme.artdiary.exhibitions.data_access.repository;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.QExhEntity;
import klieme.artdiary.exhibitions.enums.ExhField;
import klieme.artdiary.exhibitions.enums.ExhPrice;
import klieme.artdiary.exhibitions.enums.ExhState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExhRepoCustomImpl implements ExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<ExhEntity> searchExhList(String searchName, ExhField field, ExhPrice price, ExhState state,
		LocalDate date) {
		QExhEntity exh = QExhEntity.exhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (searchName != null) {
			builder.and(exh.exhName.containsIgnoreCase(searchName).or(exh.gallery.containsIgnoreCase(searchName)));
		}
		if (field != null) {
			builder.and(exh.art.eq(field.label()));
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
		if (state != null || date != null) {
			LocalDate now = date != null ? date : LocalDate.now();

			if (date != null || state == ExhState.PROCEED) {
				builder.and(exh.exhPeriodStart.loe(now)); // start <= now
				builder.and(exh.exhPeriodEnd.goe(now)); // end >= now
			} else if (state == ExhState.BEFORE_START) {
				builder.and(exh.exhPeriodStart.gt(now)); // start > now
			} else {
				builder.and(exh.exhPeriodEnd.lt(now)); // end < now
			}
		}
		return query.selectFrom(exh)
			.where(builder)
			.fetch();
	}
}
