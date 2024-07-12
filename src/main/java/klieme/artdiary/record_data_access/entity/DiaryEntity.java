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
@Table(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class DiaryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "diary_id", nullable = false)
	private Long diaryId;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private Double rate;
	@Column(name = "private", nullable = false)
	private Boolean diaryPrivate;
	@Column(nullable = false)
	private String contents;
	private String thumbnail;
	@Column(name = "write_date", nullable = false)
	private LocalDate writeDate;
	private String saying;
	@Column(name = "writer_id")
	private Long writerId;
	@Column(name = "exh_visit_id")
	private Long exhVisitId;

	@Builder
	public DiaryEntity(Long diaryId, String title, Double rate, Boolean diaryPrivate, String contents,
		String thumbnail, LocalDate writeDate, String saying, Long writerId, Long exhVisitId) {
		this.diaryId = diaryId;
		this.title = title;
		this.rate = rate;
		this.diaryPrivate = diaryPrivate;
		this.contents = contents;
		this.thumbnail = thumbnail;
		this.writeDate = writeDate;
		this.saying = saying;
		this.writerId = writerId;
		this.exhVisitId = exhVisitId;
	}
}
