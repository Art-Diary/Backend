package klieme.artdiary.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ArtDiaryException extends RuntimeException {
	private final HttpStatus status;
	private final String type;

	public ArtDiaryException(MessageType messageType) {
		super(messageType.getMessage());
		this.status = messageType.getStatus(); //ex) 404
		this.type = messageType.name(); //ex) NOT_FOUND
	}
}
