package klieme.artdiary.gatherings.service;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface GatheringReadUseCase {
	@Getter
	@ToString
	@Builder
	class FindGatheringResult {
		private final Long gatherId;
		private final String gatherName; // 단일 데이터일 때 사용

		public static FindGatheringResult findByGathering(GatheringEntity entity) {
			return FindGatheringResult.builder()
				.gatherId(entity.getGatherId())
				.gatherName(entity.getGatherName())
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringExhsResult {
		private final Long exhId;
		private final String exhName;
		private final String poster;
		private final Double rate;

		public static FindGatheringExhsResult findByGatheringExhs(ExhEntity entity, String poster, Double rate) {
			return FindGatheringExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.poster(poster)
				.rate(rate)
				.build();
		}
	}
}
