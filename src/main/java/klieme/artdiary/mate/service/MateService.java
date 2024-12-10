package klieme.artdiary.mate.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.mate.data_access.entity.MateEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class MateService implements MateReadUseCase, MateOperationUseCase {
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
		List<UserEntity> mateInfoList = mateRepository.getMyMateListByFromUserId(getUserId());
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		// 각 toUserId로 회원 정보 조회
		for (UserEntity user : mateInfoList) {
			results.add(FindMateResult.findByGatheringExhs(user));
		}
		return results;
	}

	@Override
	public FindIsMateResult searchNewMate(String nickname) {
		// 가져오기& 이미 내 전시메이트인 경우 보여주지 않기
		List<Map<String, Object>> mateQuery = mateRepository.getMateListForSearch(getUserId(), nickname);
		List<FindMateResult> alreadyMate = new ArrayList<>();
		List<FindMateResult> notMate = new ArrayList<>();
		Long myUserId = getUserId();

		for (Map<String, Object> query : mateQuery) {
			UserEntity userEntity = (UserEntity)query.get("userEntity");
			Boolean isMate = (Boolean)query.get("isMate");

			if (Objects.equals(myUserId, userEntity.getUserId())) {
				continue;
			}
			if (isMate) {
				alreadyMate.add(FindMateResult.findByGatheringExhs(userEntity));
			} else {
				notMate.add(FindMateResult.findByGatheringExhs(userEntity));
			}
		}
		return FindIsMateResult.findByGatheringMate(alreadyMate, notMate);
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

		List<UserEntity> allMateEntities = mateRepository.getMyMateListByFromUserId(getUserId());
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		for (UserEntity allMateEntity : allMateEntities) {
			results.add(FindMateResult.findByGatheringExhs(allMateEntity));
		}
		return results;
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}
