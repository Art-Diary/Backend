package klieme.artdiary.common;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import klieme.artdiary.common.jwt.CustomAccessDeniedHandler;
import klieme.artdiary.common.jwt.CustomAuthenticationEntryPoint;
import klieme.artdiary.common.jwt.CustomUserDetailsService;
import klieme.artdiary.common.jwt.JwtFilter;
import klieme.artdiary.common.jwt.JwtUtil;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtUtil jwtUtil;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;

	private static final String[] AUTH_WHITELIST = {
		"/users", "/users/unite", "/users/separate", "/users/reissue", "/users/test"
	};
	private static final String[] DATA_WHITELIST = {
		"/exh/data"
	};
	private static final String[] STATIC_RESOURCES = {
		"/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
	};

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// csrf, cors 설정
		http.csrf(AbstractHttpConfigurer::disable);
		http.cors(c -> {
			CorsConfigurationSource source = request -> {
				CorsConfiguration config = new CorsConfiguration();
				config.setAllowedOrigins(List.of("*"));
				config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
				config.setAllowedMethods(List.of("PATCH", "GET", "POST", "PUT", "DELETE"));
				return config;
			};
			c.configurationSource(source);
		});

		// 세션 관리 사용 x
		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
			SessionCreationPolicy.STATELESS));

		// formLogin, httpBasic, rememberMe, logout 비활성화
		http.formLogin(AbstractHttpConfigurer::disable);
		http.httpBasic(AbstractHttpConfigurer::disable);
		http.rememberMe(AbstractHttpConfigurer::disable);
		http.logout(AbstractHttpConfigurer::disable);

		// 권한 규칙 - AUTH_WHITELIST를 제외한 url은 권한 적용
		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers(HttpMethod.POST, AUTH_WHITELIST).permitAll()
			.requestMatchers(STATIC_RESOURCES).permitAll() // 정적 리소스 허용
			.requestMatchers(HttpMethod.GET, DATA_WHITELIST).permitAll()
			.requestMatchers(HttpMethod.POST, DATA_WHITELIST).permitAll()
			.anyRequest().authenticated()
		);

		// 예외 처리
		http.exceptionHandling((exceptionHandling) -> exceptionHandling
			.authenticationEntryPoint(authenticationEntryPoint)
			.accessDeniedHandler(accessDeniedHandler)
		);

		// UsernamePasswordAuthenticationFilter 전에 JwtAuthFilter 추가
		http.addFilterBefore(new JwtFilter(customUserDetailsService, jwtUtil),
			UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
