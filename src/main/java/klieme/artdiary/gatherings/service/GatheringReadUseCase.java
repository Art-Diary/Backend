package klieme.artdiary.gatherings.service;

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
}
