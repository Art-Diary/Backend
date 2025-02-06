package klieme.artdiary.exhibition.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhCategoryLinkId {
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	@Column(name = "category_id", nullable = false)
	private Long categoryId;

	@Builder
	public ExhCategoryLinkId(Long exhId, Long categoryId) {
		this.exhId = exhId;
		this.categoryId = categoryId;
	}
}
