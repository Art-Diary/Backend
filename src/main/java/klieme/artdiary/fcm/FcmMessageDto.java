package klieme.artdiary.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * FCM에 실제 전송될 데이터의 DTO
 */
@Getter
@Builder
public class FcmMessageDto {
	private boolean validateOnly;
	private FcmMessageDto.Message message;

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Message {
		private FcmMessageDto.Notification data;
		private String token;
	}

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Notification {
		private String title;
		private String body;
		private String image;
	}
}
