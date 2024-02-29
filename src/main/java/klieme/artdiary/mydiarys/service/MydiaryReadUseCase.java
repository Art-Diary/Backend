package klieme.artdiary.mydiarys.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MydiaryReadUseCase {
	List<FindMyDiaryResult> getMyDiaries(MyDiariesFindQuery query) throws IOException;

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MyDiariesFindQuery {
		private final Long exhId;
	}

	@Getter
	@ToString
	@Builder
	class FindMyDiaryResult {
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

		public static FindMyDiaryResult findByMyDiary(MydiaryEntity diary, UserEntity user, UserExhEntity userExh,
			ExhEntity exh, String thumbnail) {
			return FindMyDiaryResult.builder()
				.diaryId(diary.getSoloDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(thumbnail)
				.writeDate(diary.getWriteDate())
				.saying(diary.getSaying())
				.nickname(user.getNickname())
				.visitDate(userExh.getVisitDate())
				.exhName(exh.getExhName())
				.userExhId(userExh.getUserExhId())
				.build();
		}

		public static FindMyDiaryResult findByGatheringDiary(GatheringDiaryEntity diary, UserEntity user,
			GatheringEntity gathering, GatheringExhEntity gatheringExh, ExhEntity exh, String thumbnail) {
			return FindMyDiaryResult.builder()
				.diaryId(diary.getGatherDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(thumbnail)
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
