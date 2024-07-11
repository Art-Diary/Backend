package klieme.artdiary.exhibition.data_access.entity;

import java.time.LocalDateTime;

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
@Table(name = "search_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class SearchEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "search_id", nullable = false)
	private Long searchId;
	@Column(name = "search_name", nullable = false)
	private String searchName;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "search_time", nullable = false)
	private LocalDateTime searchTime;

	@Builder
	public SearchEntity(Long searchId, String searchName, Long userId, LocalDateTime searchTime) {
		this.searchId = searchId;
		this.searchName = searchName;
		this.userId = userId;
		this.searchTime = searchTime;
	}

	public void updateSearchEntity(LocalDateTime searchTime) {

		if (searchTime != null) {
			this.searchTime = searchTime;
		}
	}
}
