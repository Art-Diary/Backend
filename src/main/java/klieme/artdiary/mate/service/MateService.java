package klieme.artdiary.mate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.mate.data_access.entity.MateEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class MateService implements MateReadUseCase {
	private final MateRepository mateRepository;
	private final UserRepository userRepository;

	@Autowired
	public MateService(MateRepository mateRepository, UserRepository userRepository) {
		this.mateRepository = mateRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<MateReadUseCase.FindMateResult> getMateList() {
		// exh_mate 테이블에서 내 전시 메이트 리스트 조회
		List<MateEntity> mateEntities = mateRepository.findByFromUserId(getUserId());
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		// 각 toUserId로 회원 정보 조회
		for (MateEntity mate : mateEntities) {
			Optional<UserEntity> userEntity = userRepository.findByUserId(mate.getToUserId());
			/* TODO
			 * 저장된 프로필 사진 다운로드 구현
			 * 아래 코드의 null 수정 필요
			 */
			userEntity.ifPresent(user -> results.add(MateReadUseCase.FindMateResult.findByGatheringExhs(user, null)));
		}
		return results;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
