package klieme.artdiary.mate.data_access.entity;

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
@Table(name = "exh_mate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class MateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mate_id", nullable = false)
	private Long mateId;
	@Column(name = "from_user_id", nullable = false)
	private Long fromUserId;
	@Column(name = "to_user_id", nullable = false)
	private Long toUserId;

	@Builder
	public MateEntity(Long mateId, Long fromUserId, Long toUserId) {
		this.mateId = mateId;
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
	}
}
