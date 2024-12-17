package klieme.artdiary.favoriteexh.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhEntity;
import klieme.artdiary.favoriteexh.data_access.entity.FavoriteExhId;
import klieme.artdiary.favoriteexh.data_access.repository.FavoriteExhRepository;

@Service
public class FavoriteExhService implements FavoriteExhOperationUseCase, FavoriteExhReadUseCase {
	private final ExhRepository exhRepository;
	private final FavoriteExhRepository favoriteExhRepository;

	@Autowired
	public FavoriteExhService(ExhRepository exhRepository, FavoriteExhRepository favoriteExhRepository) {
		this.exhRepository = exhRepository;
		this.favoriteExhRepository = favoriteExhRepository;
	}

	@Override
	@Transactional
	public FindFavoriteExhResult createFavoriteExh(FavoriteExhCreateCommand command) {
		// 전시회가 있는지 확인
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId()).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));
		// 이미 저장한 전시회인지 확인
		Optional<FavoriteExhEntity> savedFavoriteExh = favoriteExhRepository.findByFavoriteExhId(FavoriteExhId.builder()
			.userId(getUserId())
			.exhId(exhEntity.getExhId())
			.build());

		if (savedFavoriteExh.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 없으면 저장
		FavoriteExhEntity favoriteExh = FavoriteExhEntity.builder()
			.favoriteExhId(FavoriteExhId.builder()
				.userId(getUserId())
				.exhId(exhEntity.getExhId())
				.build())
			.initDate(LocalDateTime.now())
			.build();
		favoriteExhRepository.save(favoriteExh);
		return FindFavoriteExhResult.findByFavoriteExh(favoriteExh);
	}

	@Override
	public List<FindFavoriteExhResult> getFavoriteExhs() {

		List<FindFavoriteExhResult> favorites = new ArrayList<>();
		//favoriteExh에서 userId에 해당하는 exhId 알아내기
		List<ExhEntity> exhEntityList = favoriteExhRepository.getFavoriteExhByUserId(getUserId());

		for (ExhEntity exh : exhEntityList) {
			favorites.add(FavoriteExhReadUseCase.FindFavoriteExhResult.findByFavoriteExhDetail(exh));
		}
		return favorites;
	}

	@Override
	@Transactional
	public void deleteFavoriteExh(List<FavoriteExhCreateCommand> commands) {

		for (FavoriteExhCreateCommand command : commands) {

			FavoriteExhId favoriteExhId = FavoriteExhId.builder()
				.userId(getUserId())
				.exhId(command.getExhId())
				.build();
			FavoriteExhEntity fEntity = favoriteExhRepository.findByFavoriteExhId(favoriteExhId)
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			favoriteExhRepository.delete(fEntity);
		}
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}
