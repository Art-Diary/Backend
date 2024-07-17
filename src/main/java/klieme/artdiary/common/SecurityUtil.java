package klieme.artdiary.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;

public class SecurityUtil {
	public static Long getCurrentUserId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName().equals("anonymousUser")
			|| authentication.getPrincipal().equals("anonymousUser")) {
			throw new ArtDiaryException(MessageType.ReLogin);
		}

		UserDetails principal = (UserDetails)authentication.getPrincipal();
		// System.out.println(principal.getAuthorities().stream().findFirst().get());
		return Long.parseLong(principal.getUsername());
	}
}
