package klieme.artdiary.common.push_alarm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import klieme.artdiary.fcm.FcmMessageDto;
import klieme.artdiary.fcm.FcmSendDto;

@Component
public class PushAlarm {
	@Value("${fcm.api.url}")
	private String FCM_API_URL;
	@Value("${firebase.config.path}")
	private String FIREBASE_CONFIG_PATH;

	public void sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
		String message = makeMessage(fcmSendDto);
		RestTemplate restTemplate = new RestTemplate();

		restTemplate.getMessageConverters()
			.addFirst(new StringHttpMessageConverter(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + getAccessToken());

		HttpEntity<String> entity = new HttpEntity<>(message, headers);

		String API_URL = FCM_API_URL;
		try {
			ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			System.out.println("Wrong Token");
		}
	}

	/**
	 * Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받습니다.
	 *
	 * @return Bearer token
	 */
	private String getAccessToken() throws IOException {
		String firebaseConfigPath = FIREBASE_CONFIG_PATH;

		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		googleCredentials.refreshIfExpired();
		return googleCredentials.getAccessToken().getTokenValue();
	}

	/**
	 * FCM 전송 정보를 기반으로 메시지를 구성합니다. (Object -> String)
	 *
	 * @param fcmSendDto FcmSendDto
	 * @return String
	 */
	private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
		String title = fcmSendDto.getTitle();
		
		// type
		title += "/" + fcmSendDto.getType();
		// id
		if (Objects.equals(fcmSendDto.getType(), "exhibition")) {
			title += "-" + fcmSendDto.getExhId().toString();
		} else if (Objects.equals(fcmSendDto.getType(), "gathering")) {
			title += "-" + fcmSendDto.getGatherId().toString();
		}

		FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
			.message(FcmMessageDto.Message.builder()
				.token(fcmSendDto.getToken())
				.data(FcmMessageDto.Notification.builder()
					.title(title)
					.body(fcmSendDto.getBody())
					.image(null)
					.build()
				).build()).validateOnly(false).build();
		ObjectMapper om = new ObjectMapper();

		return om.writeValueAsString(fcmMessageDto);
	}
}
