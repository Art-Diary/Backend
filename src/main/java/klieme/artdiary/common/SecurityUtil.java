package klieme.artdiary.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.jwt.CustomUserDetails;
import klieme.artdiary.user.data_access.entity.UserEntity;

public class SecurityUtil {
	public static UserEntity getCurrentUserEntity() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName().equals("anonymousUser")
			|| authentication.getPrincipal().equals("anonymousUser")) {
			throw new ArtDiaryException(MessageType.ReLogin);
		}

		CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();
		return principal.getUser();
	}
}
