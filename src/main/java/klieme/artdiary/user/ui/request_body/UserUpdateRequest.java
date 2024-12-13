package klieme.artdiary.user.ui.request_body;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class UserUpdateRequest {
	@NotBlank
	private String nickname;
	@NotBlank
	private String favoriteArt;
	private MultipartFile profile;
}
