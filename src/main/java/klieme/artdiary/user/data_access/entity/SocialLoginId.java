package klieme.artdiary.user.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialLoginId {
	@Column(name = "provider_type", nullable = false)
	private String providerType;
	@Column(name = "provider_user_id", nullable = false)
	private String providerUserId;

	@Builder
	public SocialLoginId(String providerType, String providerUserId) {
		this.providerType = providerType;
		this.providerUserId = providerUserId;
	}
}
