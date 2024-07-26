package klieme.artdiary.solo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyDiaryOperationUseCase {
	List<MyDiaryReadUseCase.FindMyDiaryResult> createMyDiary(MyDiaryCreateUpdateCommand command);

	void deleteMyDiary(Long exhId, Long diaryId);

	List<MyDiaryReadUseCase.FindMyDiaryResult> updateMyDiary(MyDiaryCreateUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class MyDiaryCreateUpdateCommand {
		private final Long exhId;
		private final Long diaryId; // update에서 사용
		private final Long exhVisitId; // 모임이 아닐 경우 -1
		private final String title;
		private final Double rate;
		private final Boolean diaryPrivate;
		private final String contents;
		private final MultipartFile thumbnail;
		private final LocalDate writeDate;
		private final String saying;
		private final MultipartFile[] files;
	}
}
