package klieme.artdiary.mydiarys.data_access.entity;

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
@Table(name = "solo_diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class MydiaryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "solo_diary_id", nullable = false)
	private Long soloDiaryId;
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
	@Column(name = "user_exh_id", nullable = false)
	private Long userExhId;

	@Builder
	public MydiaryEntity(Long soloDiaryId, String title, Double rate, Boolean diaryPrivate, String contents,
		String thumbnail, LocalDate writeDate, String saying, Long userExhId) {
		this.soloDiaryId = soloDiaryId;
		this.title = title;
		this.rate = rate;
		this.diaryPrivate = diaryPrivate;
		this.contents = contents;
		this.thumbnail = thumbnail;
		this.writeDate = writeDate;
		this.saying = saying;
		this.userExhId = userExhId;
	}

	public void updateDiary(MydiaryEntity entity) {
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
		if (entity.getThumbnail() != null) {
			this.thumbnail = entity.getThumbnail();
		}
		this.saying = entity.getSaying();
	}
}
