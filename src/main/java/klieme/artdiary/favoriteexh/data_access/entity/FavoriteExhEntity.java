package klieme.artdiary.favoriteexh.data_access.entity;

import java.time.LocalDateTime;

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
@Table(name = "favorite_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class FavoriteExhEntity {
	@EmbeddedId
	private FavoriteExhId favoriteExhId;
	@Column(name = "init_date", nullable = false)
	private LocalDateTime initDate;

	@Builder
	public FavoriteExhEntity(FavoriteExhId favoriteExhId, LocalDateTime initDate) {
		this.favoriteExhId = favoriteExhId;
		this.initDate = initDate;
	}
}
