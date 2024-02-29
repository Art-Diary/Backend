package klieme.artdiary.mate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.ImageTransfer;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.mate.data_access.entity.MateEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.users.data_access.entity.UserEntity;
import klieme.artdiary.users.data_access.repository.UserRepository;

@Service
public class MateService implements MateReadUseCase, MateOperationUseCase {
	private final MateRepository mateRepository;
	private final UserRepository userRepository;
	private final ImageTransfer imageTransfer;

	@Autowired
	public MateService(MateRepository mateRepository, UserRepository userRepository, ImageTransfer imageTransfer) {
		this.mateRepository = mateRepository;
		this.userRepository = userRepository;
		this.imageTransfer = imageTransfer;
	}

	@Override
	public List<MateReadUseCase.FindMateResult> getMateList() throws IOException {
		// exh_mate 테이블에서 내 전시 메이트 리스트 조회
		List<MateEntity> mateEntities = mateRepository.findByFromUserId(getUserId());
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		// 각 toUserId로 회원 정보 조회
		for (MateEntity mate : mateEntities) {
			Optional<UserEntity> userEntity = userRepository.findByUserId(mate.getToUserId());

			if (userEntity.isPresent()) {
				String profile = imageTransfer.downloadImage(userEntity.get().getProfile());
				results.add(MateReadUseCase.FindMateResult.findByGatheringExhs(userEntity.get(), profile));
			}
		}
		return results;
	}

	@Override
	public List<MateReadUseCase.FindMateResult> searchNewMate(String nickname) throws IOException {
		// 가져오기& 이미 내 전시메이트인 경우 보여주지 않기
		List<MateReadUseCase.FindMateResult> results = new ArrayList<>();
		List<MateEntity> mates = mateRepository.findByFromUserId(getUserId()); //나의 전시메이트 목록
		List<UserEntity> users = userRepository.findByNicknameContainingIgnoreCase(nickname);

		for (UserEntity user : users) {

			Optional<MateEntity> filterUser = mates.stream()
				.filter(m -> m.getToUserId().equals(user.getUserId()))
				.findAny();

			if (filterUser.isEmpty() && !user.getUserId().equals(getUserId())) {
				String profile = imageTransfer.downloadImage(user.getProfile());
				results.add(MateReadUseCase.FindMateResult.findByGatheringExhs(user, profile));
			}

		}

		return results;
	}

	@Override
	@Transactional
	public List<MateReadUseCase.FindMateResult> addMyMateCreate(MateOperationUseCase.AddMyMateCreateDummy dummy) throws
		IOException {

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
			String profile = imageTransfer.downloadImage(tmp.getProfile());
			MateReadUseCase.FindMateResult result = MateReadUseCase.FindMateResult.builder()
				.userId(tmp.getUserId())
				.nickname(tmp.getNickname())
				.profile(profile)
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
