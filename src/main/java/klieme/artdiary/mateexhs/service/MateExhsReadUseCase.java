package klieme.artdiary.mateexhs.service;

import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateExhsReadUseCase {
	List<FindMateExhsResult> getMateExhsList(MateExhsFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MateExhsFindQuery {
		private final Long mateId;
	}

	@Getter
	@ToString
	@Builder
	class FindMateExhsResult {
		private final Long exhId;
		private final String exhName;
		private final String poster;
		private final Double rate; //별점 평균

		@Builder
		public static FindMateExhsResult findMateExhs(ExhEntity entity, String poster, Double rate) {
			return FindMateExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.poster(poster)
				.rate(rate)
				.build();
		}
	}
}
