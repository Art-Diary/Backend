package klieme.artdiary.gathering.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.info.ExhibitionInfo;
import klieme.artdiary.gathering.info.MateInfo;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringReadUseCase {

	List<FindGatheringResult> getGatheringList();

	List<FindGatheringDiaryResult> getDiariesAboutGatheringExh(GatheringDiariesFindQuery query) throws IOException;

	FindGatheringDetailInfoResult getGatheringDetailInfo(GatheringDetailInfoFindQuery query) throws IOException;

	List<FindGatheringMatesResult> searchNicknameNotInGathering(GatheringNicknameFindQuery query) throws IOException;

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringDiariesFindQuery {
		private final Long exhId;
		private final Long gatherId;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringDetailInfoFindQuery {
		private final Long gatherId;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringNicknameFindQuery {
		private final Long gatherId;
		private final String nickname;
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
	class FindGatheringExhResult {
		private final Long exhId;
		private final String exhName;
		private final String poster;
		private final Double rate;

		public static FindGatheringExhResult findByGatheringExh(ExhEntity entity, String poster, Double rate) {
			return FindGatheringExhResult.builder()
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
		private final Long userId;
		private final String nickname; // 작성자
		private final String gatherName; // 개인일 경우 null
		private final LocalDate visitDate;
		private final String exhName;
		private final Long exhVisitId;
		private final LocalDate initDate;

		public static FindGatheringDiaryResult findByGatheringDiary(DiaryEntity diary, ExhVisitEntity exhVisit,
			GatheringEntity gathering, UserEntity user, ExhEntity exh, String thumbnail) {
			return FindGatheringDiaryResult.builder()
				.diaryId(diary.getDiaryId())
				.title(diary.getTitle())
				.rate(diary.getRate())
				.diaryPrivate(diary.getDiaryPrivate())
				.contents(diary.getContents())
				.thumbnail(thumbnail)
				.writeDate(diary.getWriteDate())
				.saying(diary.getSaying())
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.gatherName(gathering.getGatherName())
				.visitDate(exhVisit.getVisitDate())
				.exhName(exh.getExhName())
				.exhVisitId(diary.getExhVisitId())
				.initDate(diary.getInitDate())
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringMatesResult {
		private final Long userId;
		private final String nickname;
		private final String profile;
		private final String favoriteArt;

		public static FindGatheringMatesResult findByGatheringMates(UserEntity user, String profile) {
			return FindGatheringMatesResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(profile)
				.favoriteArt(user.getFavoriteArt())
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringDetailInfoResult {
		private final List<MateInfo> mates;
		private final List<ExhibitionInfo> exhibitions;

		public static FindGatheringDetailInfoResult findByGatheringDetailInfo(List<MateInfo> mates,
			List<ExhibitionInfo> exhibitions) {
			return FindGatheringDetailInfoResult.builder()
				.mates(mates)
				.exhibitions(exhibitions)
				.build();
		}
	}
}
