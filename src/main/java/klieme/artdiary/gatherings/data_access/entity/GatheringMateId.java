package klieme.artdiary.gatherings.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringMateId {
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "gather_id", nullable = false)
	private Long gatherId;

	@Builder
	public GatheringMateId(Long userId, Long gatherId) {
		this.userId = userId;
		this.gatherId = gatherId;
	}
}
