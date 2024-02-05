package klieme.artdiary.gatherings.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gathering_mate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class GatheringMateEntity {
	@EmbeddedId
	private GatheringMateId gatheringMateId;

	@Builder
	public GatheringMateEntity(GatheringMateId gatheringMateId) {
		this.gatheringMateId = gatheringMateId;
	}
}
