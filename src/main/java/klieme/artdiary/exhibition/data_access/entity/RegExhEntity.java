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
	@Column(name = "reg_exh_name", nullable = false)
	private String regExhName;
	@Column(name = "reg_gallery")
	private String regGallery;
	@Column(name = "reg_exh_period_start")
	private LocalDate regExhPeriodStart;
	@Column(name = "reg_exh_period_end")
	private LocalDate regExhPeriodEnd;
	@Column(name = "reg_painter")
	private String regPainter;
	@Column(name = "reg_fee")
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
	private Boolean regState;

	@Builder
	public RegExhEntity(Long regExhId, Long userId, String regExhName, String regGallery, LocalDate regExhPeriodStart,
		LocalDate regExhPeriodEnd, String regPainter, Integer regFee, String regIntro, String regUrl, String regPoster,
		String regArt, LocalDateTime regDate, String regComment, Boolean regState) {
		this.regExhId = regExhId;
		this.userId = userId;
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
	}
}
