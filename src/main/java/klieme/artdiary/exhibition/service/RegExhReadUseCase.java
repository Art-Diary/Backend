package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;

import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface RegExhReadUseCase {
	List<FindRegExhListResult> getRegisteredExhibitionList(Boolean isAdmin);

	RegExhReadUseCase.FindRegExhResult getRegisteredExhibition(Long regExhId, Boolean isAdmin);

	@Getter
	@ToString
	@Builder
	class FindRegExhResult {
		private final Long regExhId;
		private final String regExhName;
		private final String regGallery;
		private final String regExhPeriodStart;
		private final String regExhPeriodEnd;
		private final String regPainter;
		private final Integer regFee;
		private final String regIntro;
		private final String regUrl;
		private final String regPoster;
		private final String regArt;
		private final String regDate;
		private final String regComment;
		private final String regState;
		private final String regSource;

		public static FindRegExhResult findByRegExh(RegExhEntity regExh) {
			return FindRegExhResult.builder()
				.regExhId(regExh.getRegExhId())
				.regExhName(regExh.getRegExhName())
				.regGallery(regExh.getRegGallery())
				.regExhPeriodStart(changeDateFormat(regExh.getRegExhPeriodStart()))
				.regExhPeriodEnd(changeDateFormat(regExh.getRegExhPeriodEnd()))
				.regPainter(regExh.getRegPainter())
				.regFee(regExh.getRegFee())
				.regIntro(regExh.getRegIntro())
				.regUrl(regExh.getRegUrl())
				.regPoster(regExh.getRegPoster())
				.regArt(regExh.getRegArt())
				.regDate(changeDateTimeFormat(regExh.getRegDate()))
				.regComment(regExh.getRegComment())
				.regState(regExh.getRegState())
				.regSource(regExh.getRegSource())
				.build();
		}

	}

	@Getter
	@ToString
	@Builder
	class FindRegExhListResult {
		private final Long no;
		private final Long regExhId;
		private final String regExhName;
		private final String regNickName;
		private final String regDate;
		private final String regState;

		public static FindRegExhListResult findByRegExhList(RegExhEntity regExh, Long no, String nickname) {
			return FindRegExhListResult.builder()
				.no(no)
				.regExhId(regExh.getRegExhId())
				.regExhName(regExh.getRegExhName())
				.regNickName(nickname)
				.regDate(changeDateTimeFormat(regExh.getRegDate()))
				.regState(regExh.getRegState())
				.build();
		}
	}
}
