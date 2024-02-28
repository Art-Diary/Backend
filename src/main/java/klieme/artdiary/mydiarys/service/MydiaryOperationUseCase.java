package klieme.artdiary.mydiarys.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MydiaryOperationUseCase {
	List<MydiaryReadUseCase.FindMyDiaryResult> createMyDiary(MyDiaryCreateUpdateCommand command) throws IOException;

	void deleteMyDiary(Long exhId, Boolean solo, Long diaryId);

	List<MydiaryReadUseCase.FindMyDiaryResult> updateMyDiary(MyDiaryCreateUpdateCommand command) throws IOException;

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class MyDiaryCreateUpdateCommand {
		private final Long exhId;
		private final Long diaryId; // update에서 사용
		private final Long userExhId; // 개인 일정이 아닌 경우 -1
		private final Long gatheringExhId; // 모임이 아닐 경우 -1
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final MultipartFile thumbnail;
		private final LocalDate writeDate;
		private final String saying;
	}
}
