package klieme.artdiary.gatherings.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringReadUseCase {

	List<FindGatheringResult> getGatheringList();

	List<FindGatheringDiaryResult> getDiariesAboutGatheringExh(GatheringDiariesFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringDiariesFindQuery {
		private final Long exhId;
		private final Long gatherId;
	}

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

	@Getter
	@ToString
	@Builder
	class FindGatheringDiaryResult {
		private final Long diaryId;
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final String thumbnail;
		private final LocalDate writeDate;
		private final String saying;
		private final String nickname; // 작성자
		private final String gatherName;
		private final LocalDate visitDate;
		private final String exhName;
		private final Long gatheringExhId;

		public static FindGatheringDiaryResult findByGatheringDiary(GatheringDiaryEntity gatheringDiary,
			GatheringExhEntity gatheringExh, GatheringEntity gathering, UserEntity user, ExhEntity exh) {
			return FindGatheringDiaryResult.builder()
				.diaryId(gatheringDiary.getGatherDiaryId())
				.title(gatheringDiary.getTitle())
				.rate(gatheringDiary.getRate())
				.diaryPrivate(gatheringDiary.getDiaryPrivate())
				.contents(gatheringDiary.getContents())
				.thumbnail(gatheringDiary.getThumbnail())
				.writeDate(gatheringDiary.getWriteDate())
				.saying(gatheringDiary.getSaying())
				.nickname(user.getNickname())
				.gatherName(gathering.getGatherName())
				.visitDate(gatheringExh.getVisitDate())
				.exhName(exh.getExhName())
				.gatheringExhId(gatheringDiary.getGatheringExhId())
				.build();
		}
	}
}
