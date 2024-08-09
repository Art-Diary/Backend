package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.SecurityUtil.*;
import static klieme.artdiary.exhibition.data_access.entity.QRegExhEntity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;
import klieme.artdiary.exhibition.data_access.repository.RegExhRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class RegExhService implements RegExhOperationUseCase, RegExhReadUseCase {
	private final RegExhRepository regExhRepository;
	private final UserRepository userRepository;

	@Autowired
	public RegExhService(RegExhRepository regExhRepository, UserRepository userRepository) {
		this.regExhRepository = regExhRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	@Override
	public FindRegExhResult createRegExhByUser(RegExhCreateUpdateByUserCommand command) {

		RegExhEntity regExhEntity = RegExhEntity.builder()
			.userId(getUserId())
			.regExhName(command.getRegExhName())
			.regGallery(command.getRegGallery())
			.regExhPeriodStart(command.getRegExhPeriodStart())
			.regExhPeriodEnd(command.getRegExhPeriodEnd())
			.regPainter(command.getRegPainter())
			.regFee(command.getRegFee())
			.regIntro(command.getRegIntro())
			.regUrl(command.getRegUrl())
			.regPoster(command.getRegPoster())
			.regArt(command.getRegArt())
			.regDate(command.getRegDate())
			.regState(false).build();

		regExhRepository.save(regExhEntity);

		return FindRegExhResult.findByRegExh(regExhEntity);

	}

	@Override
	public List<FindRegExhListResult> getRegisteredExhibitionList(Boolean isAdmin) {
		List<FindRegExhListResult> results = new ArrayList<>();

		if (!isAdmin) {
			/* 사용자
			 * 1. 사용자 자격인지 확인
			 * 2. 해당 사용자가 등록 요청한 전시회 리스트
			 * */
			// TODO 1. 사용자 자격인지 확인
			// 2. 해당 사용자가 등록 요청한 전시회 리스트
			UserEntity user = userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(
				MessageType.NOT_FOUND));
			List<RegExhEntity> regExhEntityList = regExhRepository.findByUserId(getUserId());
			Long idx = 1L;

			for (RegExhEntity regExhEntity : regExhEntityList) {
				results.add(FindRegExhListResult.findByRegExhList(regExhEntity, idx, user.getNickname()));
				idx++;
			}
		} else {
			/* 관리자
			 * 1. 관리자 자격인지 확인
			 * 2. 등록을 기다리는 전시회 리스트
			 * */
			// TODO 1. 관리자 자격인지 확인
			// 2. 등록을 기다리는 전시회 리스트
			List<Map<String, Object>> queryResultList = regExhRepository.getRegExhListByAdmin();
			Long idx = 1L;

			for (Map<String, Object> queryResult : queryResultList) {
				RegExhEntity regExh = (RegExhEntity)queryResult.get("regExhEntity");
				UserEntity user = (UserEntity)queryResult.get("userEntity");
				results.add(FindRegExhListResult.findByRegExhList(regExh, idx, user.getNickname()));
				idx++;
			}
		}
		return results;
	}

	@Override
	public FindRegExhResult getRegisteredExhibition(Long regExhId) {

		// RegExhEntity entity=regExhRepository.findByRegExhId(regExhId).orElseThrow(() -> new ArtDiaryException(
		// 	MessageType.NOT_FOUND));
		//
		// return FindRegExhResult.findByRegExh(entity);
		return null;
	}

	@Transactional
	@Override
	public FindRegExhResult updateRegExhByUser(RegExhCreateUpdateByUserCommand command) {

		RegExhEntity regExhEntity = regExhRepository.findByRegExhId(command.getRegExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		regExhEntity.updateRegExh(RegExhEntity.builder()
			.userId(getUserId())
			.regExhName(command.getRegExhName())
			.regGallery(command.getRegGallery())
			.regExhPeriodStart(command.getRegExhPeriodStart())
			.regExhPeriodEnd(command.getRegExhPeriodEnd())
			.regPainter(command.getRegPainter())
			.regFee(command.getRegFee())
			.regIntro(command.getRegIntro())
			.regUrl(command.getRegUrl())
			.regPoster(command.getRegPoster())
			.regArt(command.getRegArt())
			.regDate(command.getRegDate())
			.regState(false).build());

		regExhRepository.save(regExhEntity);

		return FindRegExhResult.findByRegExh(regExhEntity);
	}

	@Transactional
	@Override
	public void deleteRegExhByUser(Long regExhId) {
		// TODO 사용자 자격인지 확인
		// regExhId가 해당 사용자의 것인지 확인
		RegExhEntity regExhEntity = regExhRepository.findByRegExhId(regExhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (!Objects.equals(regExhEntity.getUserId(), getUserId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// regState 확인하여 등록이 완료된 전시회인지 확인
		if (regExhEntity.getRegState()) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
		// 삭제
		regExhRepository.deleteById(regExhId);
	}

	@Transactional
	@Override
	public FindRegExhResult confirmExhRequestByAdmin(RegExhUpdateByAdminCommand command) {
		return null;
	}

	private Long getUserId() {
		return getCurrentUserId();
	}
}
