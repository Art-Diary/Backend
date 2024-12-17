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
	@Column(name = "favorite_exh_alarm", nullable = false)
	private Boolean favoriteExhAlarm;
	@Column(name = "visit_solo_alarm", nullable = false)
	private Boolean visitSoloAlarm;
	@Column(name = "visit_gathering_alarm", nullable = false)
	private Boolean visitGatheringAlarm;
	@Column(name = "new_gathering_alarm", nullable = false)
	private Boolean newGatheringAlarm;
	@Column(name = "new_date_gathering_alarm", nullable = false)
	private Boolean newDateGatheringAlarm;
	@Column(name = "refresh_token")
	private String refreshToken;
	@Column(name = "alarm_token")
	private String alarmToken;
	@Column(name = "provider_type", nullable = false)
	private String providerType;
	@Column(name = "role_type", nullable = false)
	private String roleType;

	@Builder
	public UserEntity(Long userId, String email, String nickname, String profile, String favoriteArt,
		Boolean favoriteExhAlarm, Boolean visitSoloAlarm, Boolean visitGatheringAlarm, Boolean newGatheringAlarm,
		Boolean newDateGatheringAlarm, String refreshToken, String alarmToken, String providerType, String roleType) {
		this.userId = userId;
		this.email = email;
		this.nickname = nickname;
		this.profile = profile;
		this.favoriteArt = favoriteArt;
		this.favoriteExhAlarm = favoriteExhAlarm;
		this.visitSoloAlarm = visitSoloAlarm;
		this.visitGatheringAlarm = visitGatheringAlarm;
		this.newGatheringAlarm = newGatheringAlarm;
		this.newDateGatheringAlarm = newDateGatheringAlarm;
		this.refreshToken = refreshToken;
		this.alarmToken = alarmToken;
		this.providerType = providerType;
		this.roleType = roleType;
	}

	public void updateUser(UserEntity user) {
		if (user.getNickname() != null) {
			this.nickname = user.getNickname();
		}
		this.profile = user.getProfile();
		if (user.getFavoriteArt() != null) {
			this.favoriteArt = user.getFavoriteArt();
		}
		if (user.getFavoriteExhAlarm() != null) {
			this.favoriteExhAlarm = user.getFavoriteExhAlarm();
		}
		if (user.getVisitSoloAlarm() != null) {
			this.visitSoloAlarm = user.getVisitSoloAlarm();
		}
		if (user.getVisitGatheringAlarm() != null) {
			this.visitGatheringAlarm = user.getVisitGatheringAlarm();
		}
		if (user.getNewGatheringAlarm() != null) {
			this.newGatheringAlarm = user.getNewGatheringAlarm();
		}
		if (user.getNewDateGatheringAlarm() != null) {
			this.newDateGatheringAlarm = user.getNewDateGatheringAlarm();
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
