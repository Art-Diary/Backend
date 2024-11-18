package klieme.artdiary.solo.service;

import static klieme.artdiary.common.FormatDate.*;

import java.io.IOException;
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

public interface MyDiaryReadUseCase {
	List<FindMyDiaryResult> getMyDiaries(MyDiariesFindQuery query) throws IOException;

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MyDiariesFindQuery {
		private final Long exhId;
		private final Boolean forget;
		private final LocalDate visitDate;
		private final Long gatherId;
	}

	@Getter
	@ToString
	@Builder
	class FindMyDiaryResult {
		private final Long diaryId;
		private final Long exhVisitId;
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

		public static FindMyDiaryResult findByMyDiary(UserEntity user, ExhEntity exh, ExhVisitEntity exhVisit,
			DiaryEntity diary, GatheringEntity gathering) {

			return FindMyDiaryResult.builder()
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
