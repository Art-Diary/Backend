package klieme.artdiary.exhibitions.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.enums.ExhField;
import klieme.artdiary.exhibitions.enums.ExhPrice;
import klieme.artdiary.exhibitions.enums.ExhState;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.users.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ExhReadUseCase {
	FindStoredDateResult getStoredDateOfExhs(StoredDateFindQuery query);

	List<FindExhResult> getExhList(ExhListFindQuery query);

	FindExhResult getExhDetailInfo(Long exhId);

	List<FindDiaryResult> getAllOfExhIdDiaries(Long exhId);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class ExhListFindQuery {
		private final String searchName;
		private final ExhCategory exhCategory;
		private final LocalDate date;

		@Builder
		@Getter
		public static class ExhCategory {
			private final ExhField field;
			private final ExhPrice price;
			private final ExhState state;
		}
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
		private final LocalDate visitDate; // 단일 데이터일 때 사용
		private final List<LocalDate> dates; // 리스트 데이터일 떄 사용

		public static FindStoredDateResult findByStoredDate(Long exhId, LocalDate visitDate, List<LocalDate> dates) {
			return FindStoredDateResult.builder()
				.exhId(exhId)
				.visitDate(visitDate)
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
		private final LocalDate exhPeriodStart;
		private final LocalDate exhPeriodEnd;
		private final String poster;
		private final Boolean favoriteExh;
		private final String painter;
		private final Integer fee;
		private final String intro;
		private final String url;
		private final String art;

		/* poster 왜 따로 빼는지 궁금
			public static FindExhResult findByExh(ExhEntity exh, Boolean favoriteExh, String poster) {
				return FindExhResult.builder()
					.exhId(exh.getExhId())
					.exhName(exh.getExhName())
					.gallery(exh.getGallery())
					.exhPeriodStart(exh.getExhPeriodStart())
					.exhPeriodEnd(exh.getExhPeriodEnd())
					.poster(poster)
					.favoriteExh(favoriteExh)
					.painter(exh.getPainter())
					.fee(exh.getFee())
					.intro(exh.getIntro())
					.url(exh.getUrl())
					.build();
			}
	*/
		public static FindExhResult findByExh(ExhEntity exh, Boolean favoriteExh) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(exh.getPoster())
				.favoriteExh(favoriteExh)
				.painter(exh.getPainter())
				.fee(exh.getFee())
				.intro(exh.getIntro())
				.url(exh.getUrl())
				.art(exh.getArt())
				.build();
		}

		public static FindExhResult findByExhForList(ExhEntity exh, Boolean isFavoriteExh, String poster) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(exh.getExhPeriodStart())
				.exhPeriodEnd(exh.getExhPeriodEnd())
				.poster(poster)
				.favoriteExh(isFavoriteExh)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindDiaryResult {
		private final Long diaryId;
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final String thumbnail;
		private final LocalDate writeDate;
		private final String saying;
		private final String nickname; // 작성자
		private final String gatherName; //일단 개인일정인 경우 null로, findSoloDiary에서 없음.
		private final LocalDate visitDate;
		private final String exhName;
		private final Long userExhId; // 개인 일정이 아닌 경우 null
		private final Long gatheringExhId; // 모임이 아닐 경우 null

		public static FindDiaryResult findSoloDiary(MydiaryEntity diary, UserExhEntity userexh, UserEntity user,
			ExhEntity exh) {
			return FindDiaryResult.builder()
				.diaryId(diary.getSoloDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(diary.getThumbnail())
				.writeDate(diary.getWriteDate())
				.saying(diary.getSaying())
				.nickname(user.getNickname())
				.visitDate(userexh.getVisitDate())
				.exhName(exh.getExhName())
				.userExhId(userexh.getUserExhId())
				.build();
		}

		public static FindDiaryResult findGatheringDiary(GatheringDiaryEntity diary, GatheringExhEntity gatherexh,
			GatheringEntity gather, UserEntity user, ExhEntity exh) {
			return FindDiaryResult.builder()
				.diaryId(diary.getGatherDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(diary.getThumbnail())
				.writeDate(diary.getWriteDate())
				.saying(diary.getSaying())
				.nickname(user.getNickname())
				.gatherName(gather.getGatherName())
				.visitDate(gatherexh.getVisitDate())
				.exhName(exh.getExhName())
				.gatheringExhId(gatherexh.getGatheringExhId())
				.build();
		}
	}
}
