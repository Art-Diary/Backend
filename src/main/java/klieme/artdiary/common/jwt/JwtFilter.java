package klieme.artdiary.common.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행 보장

	private final CustomUserDetailsService customUserDetailsService;
	private final JwtUtil jwtUtil;

	/**
	 * JWT 토큰 검증 필터 수행
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");

		// jwt 헤더 검사
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			// jwt 유효성 검사
			if (jwtUtil.validateToken(token, false)) {
				Long userId = jwtUtil.getUserId(token);

				// 유효한 토큰이면 userDetails 생성
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

				if (userDetails != null) {
					// (UserDetsils, Password, Role)를 통한 접근 권한 인증 Token 생성
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					// 현재 request의 Security Context에 접근 권한 설정
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
		}
		filterChain.doFilter(request, response); // 다음 필터로 넘기기
	}
}