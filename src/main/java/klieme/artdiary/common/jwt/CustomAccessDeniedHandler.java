package klieme.artdiary.common.jwt;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import klieme.artdiary.common.api.MessageType;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	/**
	 * 인증이 완료되었으나 해당 엔드포인트에 접근할 권한이 없다면,
	 * 403 Forbidden 오류가 발생한다.
	 */
	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException
	) throws IOException {
		JSONObject json = new JSONObject();

		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		json.put("errorType", "FORBIDDEN");
		json.put("errorMessage", MessageType.FORBIDDEN.getMessage());
		response.getWriter().print(json);
	}
}
