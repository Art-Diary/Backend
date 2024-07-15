package klieme.artdiary.record_data_access.repository;

import java.util.List;
import java.util.Map;

public interface DiaryRepoCustom {
	List<Map<String, Object>> getMyDiarySumRateAndCount(Long userId);
}
