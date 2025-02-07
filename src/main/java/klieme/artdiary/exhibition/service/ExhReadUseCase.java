package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import klieme.artdiary.exhibition.info.StoredListOfDate;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ExhReadUseCase {
	FindStoredDateResult getStoredDateOfExhsByGatherId(StoredDateFindQuery query);

	List<FindExhResult> getExhList(ExhListFindQuery query);

	List<FindExhResult> getExhListBySearchName(String searchName);

	FindExhResult getExhDetailInfo(Long exhId);

	List<FindDiaryResult> getAllOfExhIdDiaries(Long exhId);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class ExhListFindQuery {
		private final String searchName;
		private final List<ExhField> fieldList;
		private final ExhPrice price;
		private final List<ExhState> stateList;
		private final LocalDate date;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class StoredDateFindQuery {
		private final Long exhId;
		private final Long gatherId;
	}

	@Getter
	@ToString
	@Builder
	class FindStoredDateResult {
		private final Long exhId;
		private final Long gatherId; // 개인일 경우엔 null
		private final String gatherName; // 개인일 경우엔 null
		private final List<StoredListOfDate> dates;

		public static FindStoredDateResult findByStoredDate(Long exhId, GatheringEntity gathering,
			List<StoredListOfDate> dates) {
			return FindStoredDateResult.builder()
				.exhId(exhId)
				.gatherId(gathering != null ? gathering.getGatherId() : null)
				.gatherName(gathering != null ? gathering.getGatherName() : null)
				.dates(dates)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindExhResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String exhPeriodStart;
		private final String exhPeriodEnd;
		private final String poster;
		private final Boolean favoriteExh;
		private final String painter;
		private final Integer fee;
		private final String intro;
		private final String url;
		private final String art;
		private final String source;

		public static FindExhResult findByExh(ExhEntity exh, Boolean favoriteExh, String art) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(changeDateFormat(exh.getExhPeriodStart()))
				.exhPeriodEnd(changeDateFormat(exh.getExhPeriodEnd()))
				.poster(exh.getPoster())
				.favoriteExh(favoriteExh)
				.painter(exh.getPainter())
				.fee(exh.getFee())
				.intro(exh.getIntro())
				.url(exh.getUrl())
				.art(art)
				.source(exh.getSource())
				.build();
		}

		public static FindExhResult findByExhForList(ExhEntity exh, Boolean isFavoriteExh) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(changeDateFormat(exh.getExhPeriodStart()))
				.exhPeriodEnd(changeDateFormat(exh.getExhPeriodEnd()))
				.poster(exh.getPoster())
				.favoriteExh(isFavoriteExh)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindDiaryResult {
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
		private final Long userId; // 작성자. 탈퇴한 경우면 null
		private final String nickname; // 작성자. 탈퇴한 경우면 null
		private final String gatherName; // 개인 일정인 경우 null
		private final String visitDate;
		private final String exhName;

		public static FindDiaryResult findStoredDiary(DiaryEntity diary, ExhVisitEntity exhVisit, UserEntity user,
			ExhEntity exh, GatheringEntity gather) {
			return FindDiaryResult.builder()
				.diaryId(diary.getDiaryId())
				.exhVisitId(diary.getExhVisitId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(diary.getThumbnail())
				.initDate(changeDateFormat(LocalDate.from(diary.getInitDate())))
				.writeDate(changeDateFormat(diary.getWriteDate()))
				.saying(diary.getSaying())
				.userId(user != null ? user.getUserId() : null)
				.nickname(user != null ? user.getNickname() : null)
				.visitDate(changeDateFormat(exhVisit.getVisitDate()))
				.exhName(exh.getExhName())
				.gatherName(gather != null ? gather.getGatherName() : null)
				.build();
		}
	}
}
