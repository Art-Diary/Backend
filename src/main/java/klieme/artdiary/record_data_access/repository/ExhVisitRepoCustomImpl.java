package klieme.artdiary.record_data_access.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExhVisitRepoCustomImpl implements ExhVisitRepoCustom {
	private final JPAQueryFactory query;
}
