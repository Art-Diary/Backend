package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;
import klieme.artdiary.exhibition.data_access.repository.RegExhRepository;

@Service
public class RegExhService implements RegExhOperationUseCase, RegExhReadUseCase {
	private final RegExhRepository regExhRepository;

	@Autowired
	public RegExhService(RegExhRepository regExhRepository) {
		this.regExhRepository = regExhRepository;
	}

	@Transactional
	@Override
	public FindRegExhResult createRegExhByUser(RegExhCreateUpdateByUserCommand command) {


		RegExhEntity regExhEntity=RegExhEntity.builder()
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
	public List<FindRegExhListResult> getRegisteredExhibitionList() {
		return null;
	}

	@Override
	public FindRegExhResult getRegisteredExhibition(Long regExhId) {
		return null;
	}

	@Transactional
	@Override
	public FindRegExhResult updateRegExhByUser(RegExhCreateUpdateByUserCommand command) {
		return null;
	}

	@Transactional
	@Override
	public void deleteRegExhByUser(Long regExhId) {

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
