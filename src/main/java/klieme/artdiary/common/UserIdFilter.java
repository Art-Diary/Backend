package klieme.artdiary.common;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserIdFilter implements Filter {
	private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;

		String userId = httpRequest.getHeader("userId");
		System.out.println("userId: " + userId);

		userIdThreadLocal.set(Long.valueOf(userId));

		filterChain.doFilter(servletRequest, servletResponse);

		userIdThreadLocal.remove();
	}

	public static Long getUserId() {
		return userIdThreadLocal.get();
	}

	@Override
	public void destroy() {
		Filter.super.destroy();
	}
}
