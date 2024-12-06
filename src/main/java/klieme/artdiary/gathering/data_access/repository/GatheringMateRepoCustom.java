package klieme.artdiary.gathering.data_access.repository;

import java.util.List;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;

public interface GatheringMateRepoCustom {

	List<GatheringEntity> getGatheringListByRecentVisitDate(Long userId);
}
