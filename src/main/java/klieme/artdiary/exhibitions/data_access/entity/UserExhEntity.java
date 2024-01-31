package klieme.artdiary.exhibitions.data_access.entity;

import java.time.LocalDate;

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
@Table(name = "user_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserExhEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_exh_id", nullable = false)
	private Long userExhId;
	@Column(name = "visit_date")
	private LocalDate visitDate;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "exh_id")
	private Long exhId;

	@Builder
	public UserExhEntity(Long userExhId, LocalDate visitDate, Long userId, Long exhId) {
		this.userExhId = userExhId;
		this.visitDate = visitDate;
		this.userId = userId;
		this.exhId = exhId;
	}
}
