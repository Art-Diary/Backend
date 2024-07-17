package klieme.artdiary.favoriteexh.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.favoriteexh.service.FavoriteExhOperationUseCase;
import klieme.artdiary.favoriteexh.service.FavoriteExhReadUseCase;
import klieme.artdiary.favoriteexh.ui.request_body.DeleteFavoriteExhsRequest;
import klieme.artdiary.favoriteexh.ui.request_body.FavoriteExhRequest;
import klieme.artdiary.favoriteexh.ui.view.FavoriteExhView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/favorites")
public class FavoriteExhController {
	private final FavoriteExhOperationUseCase favoriteExhOperationUseCase;
	private final FavoriteExhReadUseCase favoriteExhReadUseCase;

	@Autowired
	public FavoriteExhController(FavoriteExhOperationUseCase favoriteExhOperationUseCase,
		FavoriteExhReadUseCase favoriteExhReadUseCase) {
		this.favoriteExhOperationUseCase = favoriteExhOperationUseCase;
		this.favoriteExhReadUseCase = favoriteExhReadUseCase;
	}

	/**
	 * 좋아요 전시회 목록 조회
	 * "/favorites"
	 */
	@GetMapping("")
	public ResponseEntity<List<FavoriteExhView>> getFavoriteExhList() {
		log.info("[좋아요 전시회 목록 조회]");

		List<FavoriteExhReadUseCase.FindFavoriteExhResult> results = favoriteExhReadUseCase.getFavoriteExhs();

		List<FavoriteExhView> viewResult = new ArrayList<>();

		for (FavoriteExhReadUseCase.FindFavoriteExhResult result : results) {
			viewResult.add(FavoriteExhView.builder().result(result).build());
		}

		return ResponseEntity.ok(viewResult);
	}

	@PostMapping("/like")
	public ResponseEntity<FavoriteExhView> createFavoriteExh(@Valid @RequestBody FavoriteExhRequest request) {
		log.info("[전시회 좋아요 생성 클릭]");
		// request data 저장
		var command = FavoriteExhOperationUseCase.FavoriteExhCreateCommand.builder()
			.exhId(request.getExhId())
			.build();
		// 비즈니스 로직 호출
		FavoriteExhReadUseCase.FindFavoriteExhResult result = favoriteExhOperationUseCase.createFavoriteExh(command);
		return ResponseEntity.created(null).body(FavoriteExhView.builder().result(result).build());
	}

	@PostMapping("/unlike")
	public void deleteFavoriteExh(@Valid @RequestBody DeleteFavoriteExhsRequest deleterequests) {
		log.info("[전시회 좋아요 해제 클릭]");

		List<FavoriteExhOperationUseCase.FavoriteExhCreateCommand> commands = new ArrayList<>();
		List<Long> requests = deleterequests.getFavoriteExhsList();

		//List<FavoriteExhRequest> requests=deleterequests.getFavoriteExhsList();
		for (Long request : requests) {
			var command = FavoriteExhOperationUseCase.FavoriteExhCreateCommand.builder()
				.exhId(request)
				.build();
			commands.add(command);
		}

		favoriteExhOperationUseCase.deleteFavoriteExh(commands);
	}
}
