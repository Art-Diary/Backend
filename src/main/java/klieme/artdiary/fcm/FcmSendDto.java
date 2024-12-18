package klieme.artdiary.fcm;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
 * 모바일에서 전달받은 객체를 매핑하는 DTO*/
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDto {
	private String token;
	private String title;
	private String body;
	private String type; // exhibition or gathering or calendar
	private Long exhId;
	private Long gatherId;

	@Builder(toBuilder = true)
	public FcmSendDto(String token, String title, String body, String type, Long exhId, Long gatherId) {
		this.token = token;
		this.title = title;
		this.body = body;
		this.type = type;
		this.exhId = exhId;
		this.gatherId = gatherId;
	}
}
