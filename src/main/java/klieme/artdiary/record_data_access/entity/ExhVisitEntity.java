package klieme.artdiary.record_data_access.entity;

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
@Table(name = "exh_visit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class ExhVisitEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exh_visit_id", nullable = false)
	private Long exhVisitId;
	@Column(name = "visit_date")
	private LocalDate visitDate;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "gather_id")
	private Long gatherId;
	@Column(name = "exh_id")
	private Long exhId;

	@Builder
	public ExhVisitEntity(Long exhVisitId, LocalDate visitDate, Long userId, Long gatherId, Long exhId) {
		this.exhVisitId = exhVisitId;
		this.visitDate = visitDate;
		this.userId = userId;
		this.gatherId = gatherId;
		this.exhId = exhId;
	}

	public void updateUserIdNull() {
		this.userId = null;
	}
}
