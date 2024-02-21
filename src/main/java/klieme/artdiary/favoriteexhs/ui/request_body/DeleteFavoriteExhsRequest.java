package klieme.artdiary.favoriteexhs.ui.request_body;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteFavoriteExhsRequest {

	@NotNull
	@Valid
	public List<Long> favoriteExhsList;

	/* 다음에 시도
	public static class FavoriteExhs {

		@NotNull
		public Long exhId;
	}*/
}
