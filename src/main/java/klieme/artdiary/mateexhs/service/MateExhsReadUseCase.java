package klieme.artdiary.mateexhs.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.mydiarys.service.MydiaryReadUseCase;
import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateExhsReadUseCase {
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
		public static FindMateExhsResult findMateExhs(ExhEntity entity, String poster, Double rate) {
			return FindMateExhsResult.builder()
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
	class FindMateDiaryResult {

		private final Long diaryId;
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final String thumbnail;
		private final LocalDate writeDate;
		private final String saying;
		private final String nickname;
		private final String gatherName;
		private final LocalDate visitDate;
		private final String exhName;
		private final Long userExhId;
		private final Long gatheringExhId;

		@Builder
		public static MateExhsReadUseCase.FindMateDiaryResult findMateSoloDiary(MydiaryEntity diary, UserEntity user,
			UserExhEntity userExh,
			ExhEntity exh) {
			return MateExhsReadUseCase.FindMateDiaryResult.builder()
				.diaryId(diary.getSoloDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(diary.getThumbnail())
				.writeDate(diary.getWriteDate())
				.saying(diary.getSaying())
				.nickname(user.getNickname())
				.visitDate(userExh.getVisitDate())
				.exhName(exh.getExhName())
				.userExhId(userExh.getUserExhId())
				.build();
		}

		@Builder
		public static MateExhsReadUseCase.FindMateDiaryResult findMateGatheringDiary(GatheringDiaryEntity diary,
			UserEntity user,
			GatheringEntity gathering, GatheringExhEntity gatheringExh, ExhEntity exh) {
			return MateExhsReadUseCase.FindMateDiaryResult.builder()
				.diaryId(diary.getGatherDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(diary.getThumbnail())
				.writeDate(diary.getWriteDate())
				.saying(diary.getSaying())
				.nickname(user.getNickname())
				.gatherName(gathering.getGatherName())
				.visitDate(gatheringExh.getVisitDate())
				.exhName(exh.getExhName())
				.gatheringExhId(gatheringExh.getGatheringExhId())
				.build();
		}

	}
}
