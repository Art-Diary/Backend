package klieme.artdiary.gatherings.data_access.entity;

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
@Table(name = "gathering_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class GatheringExhEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gathering_exh_id", nullable = false)
	private Long gatheringExhId;
	@Column(name = "visit_date")
	private LocalDate visitDate;
	@Column(name = "gather_id", nullable = false)
	private Long gatherId;
	@Column(name = "exh_id")
	private Long exhId;

	@Builder
	public GatheringExhEntity(Long gatheringExhId, LocalDate visitDate, Long gatherId, Long exhId) {
		this.gatheringExhId = gatheringExhId;
		this.visitDate = visitDate;
		this.gatherId = gatherId;
		this.exhId = exhId;
	}
}
