package klieme.artdiary.gatherings.data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;

public interface GatheringRepoCustom {

	List<Map<String, Object>> sumRateByGatherExhId(Long userId);

	List<Tuple> getMyDiaryListInGatheringWithJoin(Long userId, Long exhId);

	List<Tuple> getMyDiaryListWithDateInGatheringWithJoin(Long userId, Long exhId, LocalDate visitDate,
		Long gatheringExhId);
}
