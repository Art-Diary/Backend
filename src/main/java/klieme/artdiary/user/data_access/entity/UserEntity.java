package klieme.artdiary.user.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(nullable = false)
	private String email;
	@Column(nullable = false)
	private String nickname;
	@Column
	private String profile;
	@Column(name = "favorite_art")
	private String favoriteArt;
	@Column(nullable = false)
	private Boolean alarm1;
	@Column(nullable = false)
	private Boolean alarm2;
	@Column(nullable = false)
	private Boolean alarm3;
	@Column(name = "refresh_token")
	private String refreshToken;
	@Column(name = "alarm_token")
	private String alarmToken;
	@Column(name = "provider_type", nullable = false)
	private String providerType;
	@Column(name = "role_type", nullable = false)
	private String roleType;

	@Builder
	public UserEntity(Long userId, String email, String nickname, String profile, String favoriteArt, Boolean alarm1,
		Boolean alarm2, Boolean alarm3, String refreshToken, String alarmToken, String providerType, String roleType) {
		this.userId = userId;
		this.email = email;
		this.nickname = nickname;
		this.profile = profile;
		this.favoriteArt = favoriteArt;
		this.alarm1 = alarm1;
		this.alarm2 = alarm2;
		this.alarm3 = alarm3;
		this.refreshToken = refreshToken;
		this.alarmToken = alarmToken;
		this.providerType = providerType;
		this.roleType = roleType;
	}

	public void updateUser(UserEntity user) {
		if (user.getNickname() != null) {
			this.nickname = user.getNickname();
		}
		if (user.getProfile() != null) {
			System.out.println(user.getProfile());
			this.profile = user.getProfile();
		}
		if (user.getFavoriteArt() != null) {
			this.favoriteArt = user.getFavoriteArt();
		}
		if (user.getAlarm1() != null) {
			this.alarm1 = user.getAlarm1();
		}
		if (user.getAlarm2() != null) {
			this.alarm2 = user.getAlarm2();
		}
		if (user.getAlarm3() != null) {
			this.alarm3 = user.getAlarm3();
		}
		if (user.getAlarmToken() != null) {
			this.alarmToken = user.getAlarmToken();
		}
		if (user.getProviderType() != null) {
			this.providerType = user.getProviderType();
		}
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
