package klieme.artdiary.mate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateId;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.mate.data_access.entity.MateEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class MateService implements MateReadUseCase, MateOperationUseCase {
	private final MateRepository mateRepository;
	private final UserRepository userRepository;
	private final GatheringMateRepository gatheringMateRepository;

	@Autowired
	public MateService(MateRepository mateRepository, UserRepository userRepository,
		GatheringMateRepository gatheringMateRepository) {
		this.mateRepository = mateRepository;
		this.userRepository = userRepository;
		this.gatheringMateRepository = gatheringMateRepository;
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

	@Override
	public List<MateReadUseCase.FindMateResult> searchNewMate(String nickname) {
		// 가져오기& 이미 내 전시메이트인 경우 보여주지 않기
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		List<MateEntity> mates = mateRepository.findByFromUserId(getUserId()); //나의 전시메이트 목록
		List<UserEntity> users = userRepository.findByNicknameContainingIgnoreCase(nickname);

		for (UserEntity user : users) {

			Optional<MateEntity> filterUser = mates.stream()
				.filter(m -> m.getToUserId().equals(user.getUserId()))
				.findAny();

			if (filterUser.isEmpty()) {
				results.add(MateReadUseCase.FindMateResult.findByGatheringExhs(user, null)); //ToDo profile
			}

		}

		return results;
	}

	@Override
	@Transactional
	public List<MateReadUseCase.FindMateResult> addMyMateCreate(MateOperationUseCase.AddMyMateCreateDummy dummy) {

		//user에 있는지 확인
		UserEntity checkEntity = userRepository.findByUserId(dummy.getToUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		//나인지 확인
		if (getUserId().equals(dummy.getToUserId())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
		//exh_mate에서 이미 저장한 친구인지 확인
		Optional<MateEntity> entity = mateRepository.findByFromUserIdAndToUserId(getUserId(), dummy.getToUserId());

		if (entity.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		//exh_mate에 저장
		MateEntity newMate = MateEntity.builder()
			.toUserId(dummy.getToUserId())
			.fromUserId(getUserId())
			.build();

		mateRepository.save(newMate);

		List<MateEntity> allMateEntities = mateRepository.findByFromUserId(getUserId());
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		for (MateEntity allMateEntity : allMateEntities) {
			UserEntity tmp = userRepository.findByUserId(allMateEntity.getToUserId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			MateReadUseCase.FindMateResult result = MateReadUseCase.FindMateResult.builder()
				.userId(tmp.getUserId())
				.nickname(tmp.getNickname())
				.profile(tmp.getProfile())
				.favoriteArt(tmp.getFavoriteArt())
				.build();
			results.add(result);
		}

		return results;

	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
