package klieme.artdiary.mate.data_access.repository;

import java.util.List;

import klieme.artdiary.user.data_access.entity.UserEntity;

public interface MateRepoCustom {
	List<UserEntity> getMyMateListByFromUserId(Long fromUserId);
}
