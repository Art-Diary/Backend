package klieme.artdiary.user.ui.request_body;

import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class UserUpdateRequest {
	@Nullable
	private MultipartFile profile;
	@NotBlank
	private String nickname;
	@NotBlank
	private String favoriteArt;
}
