package klieme.artdiary.user.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_login")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class SocialLoginEntity {
	@EmbeddedId
	private SocialLoginId socialLoginId;
	@Column(name = "provider_access_token", nullable = false)
	private String providerAccessToken;
	@Column(name = "user_id")
	private Long userId;

	@Builder
	public SocialLoginEntity(SocialLoginId socialLoginId, String providerAccessToken, Long userId) {
		this.socialLoginId = socialLoginId;
		this.providerAccessToken = providerAccessToken;
		this.userId = userId;
	}
}
