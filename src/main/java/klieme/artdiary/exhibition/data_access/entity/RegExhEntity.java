package klieme.artdiary.exhibition.data_access.entity;

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
@Table(name = "reg_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class RegExhEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reg_exh_id", nullable = false)
	private Long regExhId;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "exh_id")
	private Long exhId;
	@Column(name = "reg_exh_name", nullable = false)
	private String regExhName;
	@Column(name = "reg_gallery", nullable = false)
	private String regGallery;
	@Column(name = "reg_exh_period_start", nullable = false)
	private LocalDate regExhPeriodStart;
	@Column(name = "reg_exh_period_end", nullable = false)
	private LocalDate regExhPeriodEnd;
	@Column(name = "reg_painter")
	private String regPainter;
	@Column(name = "reg_fee", nullable = false)
	private Integer regFee;
	@Column(name = "reg_intro")
	private String regIntro;
	@Column(name = "reg_url")
	private String regUrl;
	@Column(name = "reg_poster", nullable = false)
	private String regPoster;
	@Column(name = "reg_art")
	private String regArt;
	@Column(name = "reg_date", nullable = false)
	private LocalDateTime regDate;
	@Column(name = "reg_comment")
	private String regComment;
	@Column(name = "reg_state", nullable = false)
	private String regState;
	@Column(name = "reg_source", nullable = false)
	private String regSource;

	@Builder
	public RegExhEntity(Long regExhId, Long userId, Long exhId, String regExhName, String regGallery,
		LocalDate regExhPeriodStart, LocalDate regExhPeriodEnd, String regPainter, Integer regFee, String regIntro,
		String regUrl, String regPoster, String regArt, LocalDateTime regDate, String regComment, String regState,
		String regSource) {
		this.regExhId = regExhId;
		this.userId = userId;
		this.exhId = exhId;
		this.regExhName = regExhName;
		this.regGallery = regGallery;
		this.regExhPeriodStart = regExhPeriodStart;
		this.regExhPeriodEnd = regExhPeriodEnd;
		this.regPainter = regPainter;
		this.regFee = regFee;
		this.regIntro = regIntro;
		this.regUrl = regUrl;
		this.regPoster = regPoster;
		this.regArt = regArt;
		this.regDate = regDate;
		this.regComment = regComment;
		this.regState = regState;
		this.regSource = regSource;
	}

	public void updateRegExhByUser(RegExhEntity entity) {
		if (entity.getRegExhName() != null) {
			this.regExhName = entity.getRegExhName();
		}
		if (entity.getRegGallery() != null) {
			this.regGallery = entity.getRegGallery();
		}
		if (entity.getRegExhPeriodStart() != null) {
			this.regExhPeriodStart = entity.getRegExhPeriodStart();
		}
		if (entity.getRegExhPeriodEnd() != null) {
			this.regExhPeriodEnd = entity.getRegExhPeriodEnd();
		}
		if (entity.getRegFee() != null) {
			this.regFee = entity.getRegFee();
		}
		this.regUrl = entity.getRegUrl();
		this.regPoster = entity.getRegPoster();
		if (entity.getRegDate() != null) {
			this.regDate = entity.getRegDate();
		}
	}

	public void updateUserIdNull() {
		this.userId = null;
	}

	public void updateRegExhByAdmin(RegExhEntity entity) {
		if (entity.getExhId() != null) {
			this.exhId = entity.getExhId();
		}
		if (entity.getRegExhName() != null) {
			this.regExhName = entity.getRegExhName();
		}
		if (entity.getRegGallery() != null) {
			this.regGallery = entity.getRegGallery();
		}
		if (entity.getRegExhPeriodStart() != null) {
			this.regExhPeriodStart = entity.getRegExhPeriodStart();
		}
		if (entity.getRegExhPeriodEnd() != null) {
			this.regExhPeriodEnd = entity.getRegExhPeriodEnd();
		}
		this.regPainter = entity.getRegPainter();
		if (entity.getRegFee() != null) {
			this.regFee = entity.getRegFee();
		}
		this.regIntro = entity.getRegIntro();
		this.regUrl = entity.getRegUrl();
		if (entity.getRegPoster() != null) {
			this.regPoster = entity.getRegPoster();
		}
		if (entity.getRegArt() != null) {
			this.regArt = entity.getRegArt();
		}
		this.regComment = entity.getRegComment();
		if (entity.getRegState() != null) {
			this.regState = entity.getRegState();
		}
		if (entity.getRegSource() != null) {
			this.regSource = entity.getRegSource();
		}
	}

	public void updateRegExhPoster(String poster) {
		this.regPoster = poster;
	}
}
