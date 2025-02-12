package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.image.ImageType;
import klieme.artdiary.common.image.S3ImageTransfer;
import klieme.artdiary.exhibition.data_access.entity.CategoryEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkId;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;
import klieme.artdiary.exhibition.data_access.repository.CategoryRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhCategoryLinkRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.exhibition.data_access.repository.RegExhRepository;
import klieme.artdiary.exhibition.enums.RegExhState;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.enums.RoleType;

@Service
public class RegExhService implements RegExhOperationUseCase, RegExhReadUseCase {
	private final RegExhRepository regExhRepository;
	private final ExhRepository exhRepository;
	private final CategoryRepository categoryRepository;
	private final ExhCategoryLinkRepository exhCategoryLinkRepository;
	private final S3ImageTransfer s3ImageTransfer;

	@Autowired
	public RegExhService(RegExhRepository regExhRepository, ExhRepository exhRepository,
		CategoryRepository categoryRepository, ExhCategoryLinkRepository exhCategoryLinkRepository,
		S3ImageTransfer s3ImageTransfer) {
		this.regExhRepository = regExhRepository;
		this.exhRepository = exhRepository;
		this.categoryRepository = categoryRepository;
		this.exhCategoryLinkRepository = exhCategoryLinkRepository;
		this.s3ImageTransfer = s3ImageTransfer;
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
			.regFee(command.getRegFee())
			.regUrl(command.getRegUrl())
			.regDate(command.getRegDate())
			.regState(RegExhState.WAIT.label()).build();

		regExhRepository.save(regExhEntity);
		String poster = savePoster(command.getRegPoster(), regExhEntity);
		regExhEntity.updateRegExhPoster(poster);
		return FindRegExhResult.findByRegExh(regExhEntity);
	}

	@Override
	public List<FindRegExhListResult> getRegisteredExhibitionList(Boolean isAdmin) {
		List<FindRegExhListResult> results = new ArrayList<>();
		UserEntity user = getUser();

		if (!isAdmin) {
			/* 사용자
			 * 해당 사용자가 등록 요청한 전시회 리스트
			 * */
			List<RegExhEntity> regExhEntityList = regExhRepository.getRegExhListByUser(user.getUserId());
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
			// 1. 관리자 자격인지 확인
			if (!Objects.equals(user.getRoleType(), RoleType.ADMIN.label())) {
				throw new ArtDiaryException(MessageType.FORBIDDEN);
			}
			// 2. 등록을 기다리는 전시회 리스트
			List<Map<String, Object>> queryResultList = regExhRepository.getRegExhListByAdmin();
			Long idx = 1L;

			for (Map<String, Object> queryResult : queryResultList) {
				RegExhEntity regExhEntity = (RegExhEntity)queryResult.get("regExhEntity");
				UserEntity userEntity = (UserEntity)queryResult.get("userEntity");

				if (userEntity == null) {
					continue;
				}
				results.add(FindRegExhListResult.findByRegExhList(regExhEntity, idx, userEntity.getNickname()));
				idx++;
			}
		}
		return results;
	}

	@Override
	public FindRegExhResult getRegisteredExhibition(Long regExhId, Boolean isAdmin) {
		// 관리자 인지 확인
		if (isAdmin) {
			checkUserIsAdmin();
		}

		RegExhEntity entity = regExhRepository.findByRegExhId(regExhId).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));

		//해당 사용자가 등록한 것인지 확인
		if (!isAdmin && !Objects.equals(entity.getUserId(), getUserId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}

		return FindRegExhResult.findByRegExh(entity);

	}

	@Transactional
	@Override
	public FindRegExhResult updateRegExhByUser(RegExhCreateUpdateByUserCommand command) {

		RegExhEntity regExhEntity = regExhRepository.findByRegExhId(command.getRegExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 사용자의 것인지 확인
		if (!Objects.equals(regExhEntity.getUserId(), getUserId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		//regState==true일 경우 => 완료나 실패일 경우 이미 결정난 전시회이다.
		if (!Objects.equals(regExhEntity.getRegState(), RegExhState.WAIT.label())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
		// update reg poster
		String poster = regExhEntity.getRegPoster();
		if (command.getRegPoster() == null || !Objects.equals(command.getRegPoster().getOriginalFilename(),
			regExhEntity.getRegPoster())) {
			poster = savePoster(command.getRegPoster(), regExhEntity);
		}
		regExhEntity.updateRegExhByUser(RegExhEntity.builder()
			.regExhName(command.getRegExhName())
			.regGallery(command.getRegGallery())
			.regExhPeriodStart(command.getRegExhPeriodStart())
			.regExhPeriodEnd(command.getRegExhPeriodEnd())
			.regFee(command.getRegFee())
			.regUrl(command.getRegUrl())
			.regDate(command.getRegDate())
			.regPoster(poster)
			.build());
		regExhRepository.save(regExhEntity);
		return FindRegExhResult.findByRegExh(regExhEntity);
	}

	@Transactional
	@Override
	public void deleteRegExhByUser(Long regExhId) {
		// regExhId가 해당 사용자의 것인지 확인
		RegExhEntity regExhEntity = regExhRepository.findByRegExhId(regExhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 자신의 것이 아니라면 not found
		if (!Objects.equals(regExhEntity.getUserId(), getUserId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// regState 확인하여 등록이 완료된 전시회인지 확인 => 완료나 실패일 경우 이미 결정난 전시회이다.
		if (!Objects.equals(regExhEntity.getRegState(), RegExhState.WAIT.label())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
		regExhRepository.deleteById(regExhId);
	}

	@Transactional
	@Override
	public FindRegExhResult confirmExhRequestByAdmin(RegExhUpdateByAdminCommand command) {
		// 관리자 인지 확인
		checkUserIsAdmin();

		Map<String, Object> regExhInfo = regExhRepository.getRegExhWithExhByAdmin(command.getRegExhId());

		if (regExhInfo.isEmpty()) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		RegExhEntity regExhEntity = (RegExhEntity)regExhInfo.get("regExhEntity");
		ExhEntity exhEntity = (ExhEntity)regExhInfo.get("exhEntity");

		// update reg exh poster 포스터는 필수
		String poster = regExhEntity.getRegPoster();
		if (command.getRegPoster() != null && !Objects.equals(command.getRegPoster().getOriginalFilename(),
			regExhEntity.getRegPoster())) {
			poster = savePoster(command.getRegPoster(), regExhEntity);
		}
		if (Objects.equals(command.getRegState(), RegExhState.COMPLETE)) {
			// 관리자가 승인한 전시회를 전시회 테이블에 추가
			ExhEntity updateExhEntity = ExhEntity.builder()
				.exhName(command.getRegExhName())
				.gallery(command.getRegGallery())
				.exhPeriodStart(command.getRegExhPeriodStart())
				.exhPeriodEnd(command.getRegExhPeriodEnd())
				.painter(command.getRegPainter())
				.fee(command.getRegFee())
				.intro(command.getRegIntro())
				.url(command.getRegUrl())
				.poster(poster)
				.source(command.getRegSource())
				.build();
			if (exhEntity == null) {
				exhEntity = updateExhEntity;
			} else {
				exhEntity.updateExhEntity(updateExhEntity);
			}
			exhRepository.save(exhEntity);
			// 전시회 카테고리 추가
			List<CategoryEntity> categoryList = categoryRepository.findAll();
			List<String> fieldList = new ArrayList<>(Arrays.asList(command.getRegArt().split(","))); // 카테고리

			for (String field : fieldList) {
				CategoryEntity category = categoryList.stream()
					.filter(c -> field.equals(c.getName()))
					.findAny()
					.orElse(null);

				if (category == null) {
					continue;
				}
				exhCategoryLinkRepository.save(ExhCategoryLinkEntity.builder()
					.exhCategoryLinkId(ExhCategoryLinkId.builder()
						.exhId(exhEntity.getExhId())
						.categoryId(category.getCategoryId())
						.build())
					.build());
			}
		}
		// 사용자가 요청한 전시회를 관리자가 승인
		regExhEntity.updateRegExhByAdmin(RegExhEntity.builder()
			.exhId(Objects.equals(command.getRegState(), RegExhState.FAIL) ? null : exhEntity.getExhId())
			.regExhName(command.getRegExhName())
			.regGallery(command.getRegGallery())
			.regExhPeriodStart(command.getRegExhPeriodStart())
			.regExhPeriodEnd(command.getRegExhPeriodEnd())
			.regPainter(command.getRegPainter())
			.regFee(command.getRegFee())
			.regIntro(command.getRegIntro())
			.regUrl(command.getRegUrl())
			.regArt(command.getRegArt())
			.regComment(command.getRegComment())
			.regState(command.getRegState().label())
			.regPoster(poster)
			.regSource(command.getRegSource())
			.build());
		regExhRepository.save(regExhEntity);
		return FindRegExhResult.findByRegExh(regExhEntity);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private UserEntity getUser() {
		return getCurrentUserEntity();
	}

	private String savePoster(MultipartFile inputPoster, RegExhEntity saveEntity) {
		// 사진 업로드
		return s3ImageTransfer.uploadImageToStorage(
			S3ImageTransfer.UploadQuery.builder()
				.type(ImageType.REG_EXH)
				.image(inputPoster)
				.regExhId(saveEntity.getRegExhId())
				.build());
	}

	private void checkUserIsAdmin() {
		UserEntity user = getUser();

		if (!Objects.equals(user.getRoleType(), RoleType.ADMIN.label())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN);
		}
	}
}
