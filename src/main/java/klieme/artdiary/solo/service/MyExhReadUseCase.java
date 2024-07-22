package klieme.artdiary.solo.service;

import static klieme.artdiary.common.FormatDate.*;

import java.io.IOException;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.solo.info.StoredDateInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyExhReadUseCase {

	List<FindMyExhsResult> getMyExhsList() throws IOException;

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
		public static FindMyExhsResult findMyExhs(ExhEntity entity, double rate) {
			return FindMyExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.poster(entity.getPoster())
				.rate(rate)
				.build();
		}

		// //시도만 삭제예정
		// @Builder
		// public static FindMyExhsResult UpdateMyrate(Long exhId, String exhName, String poster,
		// 	double rate) {//MydiaryEntity,GroupDiaryEntity 둘다 사용하기 위해
		// 	return FindMyExhsResult.builder()
		// 		.exhId(exhId)
		// 		.exhName(exhName)
		// 		.poster(poster)
		// 		.rate(rate)
		// 		.build();
		// }
		//
		// public boolean equalsExhId(Object o) {
		// 	if (this == o) {
		// 		return true;
		// 	}
		//
		// 	// 비교 대상이 null이거나 형변환이 불가능한 경우에는 false를 반환
		// 	if (o == null || getClass() != o.getClass()) {
		// 		return false;
		// 	}
		//
		// 	// 필드 값을 비교하여 동등 여부를 판단
		// 	FindMyExhsResult tmp = (FindMyExhsResult)o;
		// 	return exhId.equals(tmp.exhId);
		// }
	}

	@Getter
	@ToString
	@Builder
	class FindMyStoredDateResult {
		private final Long exhId;
		// "내 기록의 전시회 방문 날짜 추가"의 반환 데이터
		private final Long exhVisitId;
		private final String visitDate;// 혜원 추가
		// "한 전시회에 대하여 캘린더에 저장된 날짜 조회"의 반환 데이터
		private final Long gatherId; // 개인일 경우엔 null
		private final String gatherName; // 개인일 경우엔 null
		private final List<StoredDateInfo> dateInfoList;

		public static FindMyStoredDateResult findByMyStoredDateSolo(Long exhId,
			List<StoredDateInfo> dateInfoList) {
			return FindMyStoredDateResult.builder()
				.exhId(exhId)
				.dateInfoList(dateInfoList)
				.build();
		}

		public static FindMyStoredDateResult findByMyStoredDateGather(Long exhId,
			GatheringEntity gathering, List<StoredDateInfo> dateInfoList) {
			return FindMyStoredDateResult.builder()
				.exhId(exhId)
				.gatherId(gathering.getGatherId())
				.gatherName(gathering.getGatherName())
				.dateInfoList(dateInfoList)
				.build();
		}

		public static FindMyStoredDateResult findByMyAllDatesSolo(ExhVisitEntity exhVisit) {
			return FindMyStoredDateResult.builder()
				.exhId(exhVisit.getExhId())
				.exhVisitId(exhVisit.getExhVisitId())
				.visitDate(changeDateFormat(exhVisit.getVisitDate()))
				.build();
		}

	}
}
