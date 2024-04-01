package klieme.artdiary.mydiarys.data_access.repository;

import java.util.List;
import java.util.Map;

public interface MydiaryRepoCustom {
	List<Map<String, Object>> sumRateByUserExhId(Long userId);
	List<Map<String, Object>> sumRateByGatherExhId(Long userId);
}
