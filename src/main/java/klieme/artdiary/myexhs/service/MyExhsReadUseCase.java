package klieme.artdiary.myexhs.service;

import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.gatherings.service.GatheringReadUseCase;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface MyExhsReadUseCase {

	List<FindMyExhsResult> getMyExhsList();

	@Getter
	@ToString
	@Builder
	class FindMyExhsResult {
		private final Long exhId;
		private final String exhName;
		private final String poster;
		private final Double rate; //별점 평균
		//	public Object equals;
		//	public boolean equals;

		@Builder
		public static FindMyExhsResult findMyExhs(ExhEntity entity,
			double rate) {//MydiaryEntity,GroupDiaryEntity 둘다 사용하기 위해
			return FindMyExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.poster(entity.getPoster())
				.rate(rate)
				.build();
		}

		//시도만 삭제예정
		@Builder
		public static FindMyExhsResult UpdateMyrate(Long exhId, String exhName, String poster,
			double rate) {//MydiaryEntity,GroupDiaryEntity 둘다 사용하기 위해
			return FindMyExhsResult.builder()
				.exhId(exhId)
				.exhName(exhName)
				.poster(poster)
				.rate(rate)
				.build();
		}

		public boolean equalsExhId(Object o) {
			if (this == o) {
				return true;
			}

			// 비교 대상이 null이거나 형변환이 불가능한 경우에는 false를 반환
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			// 필드 값을 비교하여 동등 여부를 판단
			FindMyExhsResult tmp = (FindMyExhsResult)o;
			return exhId == tmp.exhId;
		}
	}
}
