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
	private Long exhId;

	@Builder(toBuilder = true)
	public FcmSendDto(String token, String title, String body, Long exhId) {
		this.token = token;
		this.title = title;
		this.body = body;
		this.exhId = exhId;
	}
}
