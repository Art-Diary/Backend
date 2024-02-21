package klieme.artdiary.favoriteexhs.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteExhId {
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;

	@Builder
	public FavoriteExhId(Long userId, Long exhId) {
		this.userId = userId;
		this.exhId = exhId;
	}
}
