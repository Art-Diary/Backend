package klieme.artdiary.exhibition.data_access.entity;

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
@Table(name = "exhibition_category_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class ExhCategoryLinkEntity {
	@EmbeddedId
	private ExhCategoryLinkId exhCategoryLinkId;

	@Builder
	public ExhCategoryLinkEntity(ExhCategoryLinkId exhCategoryLinkId) {
		this.exhCategoryLinkId = exhCategoryLinkId;
	}
}
