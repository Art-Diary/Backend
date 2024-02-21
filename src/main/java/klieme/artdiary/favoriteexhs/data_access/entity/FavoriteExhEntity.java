package klieme.artdiary.favoriteexhs.data_access.entity;

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
@Table(name = "favorite_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class FavoriteExhEntity {
	@EmbeddedId
	private FavoriteExhId favoriteExhId;

	@Builder
	public FavoriteExhEntity(FavoriteExhId favoriteExhId) {
		this.favoriteExhId = favoriteExhId;
	}
}
