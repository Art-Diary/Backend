package klieme.artdiary.gathering.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;

public interface GatheringMateRepoCustom {
	List<GatheringEntity> getGatheringListByRecentVisitDate(Long userId);

	List<Map<String, Object>> getGatheringMateListForSearch(Long gatherId, Long userId, String nickname);

	List<Map<String, Object>> getGatheringMateList(Long gatherId);
}
