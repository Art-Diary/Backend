package klieme.artdiary.record_data_access.repository;

import java.util.List;
import java.util.Map;

public interface ExhVisitRepoCustom {
	List<Map<String, Object>>  getMyVisitedDateListOfExh(Long userId, Long exhId);
}
