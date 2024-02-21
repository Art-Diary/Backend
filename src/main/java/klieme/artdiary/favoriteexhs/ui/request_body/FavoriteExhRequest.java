package klieme.artdiary.favoriteexhs.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteExhRequest {
	@NotNull
	public Long exhId;
}
