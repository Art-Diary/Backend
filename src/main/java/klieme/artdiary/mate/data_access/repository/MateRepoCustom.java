package klieme.artdiary.mate.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.user.data_access.entity.UserEntity;

public interface MateRepoCustom {
	List<UserEntity> getMyMateListByFromUserId(Long fromUserId);

	List<Map<String, Object>> getMateListForSearch(Long fromUserId, String nickname);
}
