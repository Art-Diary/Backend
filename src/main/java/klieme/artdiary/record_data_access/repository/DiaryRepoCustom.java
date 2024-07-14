package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import klieme.artdiary.record_data_access.entity.DiaryEntity;

public interface DiaryRepoCustom {
	List<Map<String, Object>> getMyDiarySumRateAndCount(Long userId);

	List<Map<String, Object>> getDiaryList(Long userId, Long exhId, Boolean isSolo, Long gatherId, Boolean isForget,
		LocalDate visitDate);

	DiaryEntity getDiaryByDiaryIdAndWriterIdAndExhId(Long diaryId, Long writerId, Long exhId);
}
