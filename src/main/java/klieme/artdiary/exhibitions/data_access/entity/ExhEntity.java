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
@Table(name = "exhibition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class ExhEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	@Column(name = "exh_name", nullable = false)
	private String exhName;
	private String gallery;
	@Column(name = "exh_period_start")
	private LocalDate exhPeriodStart;
	@Column(name = "exh_period_end")
	private LocalDate exhPeriodEnd;
	private String painter;
	private Integer fee;
	private String intro;
	private String url;
	@Column(nullable = false)
	private String poster;

	private String art;

	@Builder
	public ExhEntity(Long exhId, String exhName, String gallery, LocalDate exhPeriodStart, LocalDate exhPeriodEnd,
		String painter, Integer fee, String intro, String url, String poster, String art) {
		this.exhId = exhId;
		this.exhName = exhName;
		this.gallery = gallery;
		this.exhPeriodStart = exhPeriodStart;
		this.exhPeriodEnd = exhPeriodEnd;
		this.painter = painter;
		this.fee = fee;
		this.intro = intro;
		this.url = url;
		this.poster = poster;
		this.art = art;
	}
}
