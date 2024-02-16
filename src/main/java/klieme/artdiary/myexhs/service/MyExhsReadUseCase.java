package klieme.artdiary.myexhs.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyExhsReadUseCase {

	List<FindMyExhsResult> getMyExhsList();

	List<FindMyStoredDateResult> getStoredDateOfExhs(MyStoredDateFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MyStoredDateFindQuery {
		private final Long exhId;
	}

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
			return exhId.equals(tmp.exhId);
		}
	}

	@Getter
	@ToString
	@Builder
	class FindMyStoredDateResult {
		private final Long exhId;
		private final Long gatherId; // 개인일 경우엔 null
		private final Long gatheringExhId; // 개인일 경우엔 null
		private final String gatherName; // 개인일 경우엔 null
		private final Long userExhId; // 모임일 경우엔 null
		private final List<LocalDate> dates;

		public static FindMyStoredDateResult findByMyStoredDateSolo(UserExhEntity userExh, List<LocalDate> dates) {
			return FindMyStoredDateResult.builder()
				.exhId(userExh.getExhId())
				.userExhId(userExh.getUserExhId())
				.dates(dates)
				.build();
		}

		public static FindMyStoredDateResult findByMyStoredDateGather(GatheringExhEntity gatheringExh,
			GatheringEntity gathering, List<LocalDate> dates) {
			return FindMyStoredDateResult.builder()
				.exhId(gatheringExh.getExhId())
				.gatherId(gathering.getGatherId())
				.gatheringExhId(gatheringExh.getGatheringExhId())
				.gatherName(gathering.getGatherName())
				.dates(dates)
				.build();
		}
	}
}
