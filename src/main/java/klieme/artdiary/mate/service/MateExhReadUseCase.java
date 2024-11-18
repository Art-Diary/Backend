package klieme.artdiary.mate.service;

import static klieme.artdiary.common.FormatDate.*;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateExhReadUseCase {
	List<FindMateExhsResult> getMateExhsList(MateExhsFindQuery query);

	List<FindMateDiaryResult> getMateDiaryList(MateDiaryFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MateExhsFindQuery {
		private final Long mateId;
		//private final Long exhId;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MateDiaryFindQuery {
		private final Long mateId;
		private final Long exhId;
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
		public static FindMateExhsResult findMateExhs(ExhEntity entity, Double rate) {
			return FindMateExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.poster(entity.getPoster())
				.rate(rate)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindMateDiaryResult {

		private final Long diaryId;
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final String thumbnail;
		private final String initDate;
		private final String writeDate;
		private final String saying;
		private final Long userId;
		private final String nickname;
		private final String gatherName;
		private final String visitDate;
		private final String exhName;
		private final Long exhVisitId;

		@Builder
		public static FindMateDiaryResult findMateDiary(DiaryEntity diary, ExhVisitEntity exhVisit, UserEntity user,
			ExhEntity exh, GatheringEntity gathering) {
			return FindMateDiaryResult.builder()
				.diaryId(diary.getDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(diary.getThumbnail())
				.initDate(changeDateFormat(LocalDate.from(diary.getInitDate())))
				.writeDate(changeDateFormat(diary.getWriteDate()))
				.saying(diary.getSaying())
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.visitDate(changeDateFormat(exhVisit.getVisitDate()))
				.exhName(exh.getExhName())
				.exhVisitId(exhVisit.getExhVisitId())
				.gatherName(gathering != null ? gathering.getGatherName() : null)
				.build();
		}
	}
}
