package klieme.artdiary.record_data_access.entity;

import java.time.LocalDate;
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
	@Column(name = "init_date", nullable = false)
	private LocalDateTime initDate;
	@Column(name = "write_date", nullable = false)
	private LocalDate writeDate;
	@Column(nullable = false)
	private String saying;
	@Column(name = "writer_id")
	private Long writerId;
	@Column(name = "exh_visit_id", nullable = false)
	private Long exhVisitId;

	@Builder
	public DiaryEntity(Long diaryId, String title, Double rate, Boolean diaryPrivate, String contents,
		String thumbnail, LocalDateTime initDate, LocalDate writeDate, String saying, Long writerId, Long exhVisitId) {
		this.diaryId = diaryId;
		this.title = title;
		this.rate = rate;
		this.diaryPrivate = diaryPrivate;
		this.contents = contents;
		this.thumbnail = thumbnail;
		this.initDate = initDate;
		this.writeDate = writeDate;
		this.saying = saying;
		this.writerId = writerId;
		this.exhVisitId = exhVisitId;
	}

	public void updateThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void updateWriterIdNull() {
		this.writerId = null;
	}

	public void updateDiary(DiaryEntity entity) {
		if (entity.getTitle() != null) {
			this.title = entity.getTitle();
		}
		if (entity.getRate() != null) {
			this.rate = entity.getRate();
		}
		if (entity.getDiaryPrivate() != null) {
			this.diaryPrivate = entity.getDiaryPrivate();
		}
		if (entity.getContents() != null) {
			this.contents = entity.getContents();
		}
		if (entity.getWriteDate() != null) {
			this.writeDate = entity.getWriteDate();
		}
		if (entity.getExhVisitId() != null) {
			this.exhVisitId = entity.getExhVisitId();
		}
		if (entity.getSaying() != null) {
			this.saying = entity.getSaying().isEmpty() ? null : entity.getSaying();
		}
	}
}
