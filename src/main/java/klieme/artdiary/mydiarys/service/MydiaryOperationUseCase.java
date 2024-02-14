package klieme.artdiary.mydiarys.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MydiaryOperationUseCase {
	List<MydiaryReadUseCase.FindMyDiaryResult> createMyDiary(MydiaryCreateCommand command);

	void deleteMyDiary(Long exhId, Boolean solo, Long diaryId);

	List<MydiaryReadUseCase.FindMyDiaryResult> updateMyDiary(MyDiaryUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class MydiaryCreateCommand {
		private final Long exhId;
		private final Long userExhId; // 개인 일정이 아닌 경우 -1
		private final Long gatheringExhId; // 모임이 아닐 경우 -1
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final String thumbnail;
		private final LocalDate writeDate;
		private final String saying;

	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class MyDiaryUpdateCommand {
		private final Long exhId;
		private final Long diaryId;
		private final Long userExhId; // 개인 일정이 아닌 경우 -1
		private final Long gatheringExhId; // 모임이 아닐 경우 -1
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final String thumbnail;
		private final LocalDate writeDate;
		private final String saying;
	}
}
