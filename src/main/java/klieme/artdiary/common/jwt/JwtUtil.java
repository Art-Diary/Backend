package klieme.artdiary.common.jwt;

import java.security.Key;
import java.sql.Date;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	private final Key secretKey;
	private final Long accessExpiredTime;
	private final Long refreshExpiredTime;

	public JwtUtil(
		@Value("${jwt.secretKey}") String secretKey,
		@Value("${jwt.accessTokenExpiredTime}") Long accessExpiredTime,
		@Value("${jwt.refreshTokenExpiredTime}") Long refreshExpiredTime
	) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		this.accessExpiredTime = accessExpiredTime;
		this.refreshExpiredTime = refreshExpiredTime;
	}

	/**
	 * JWT 생성
	 */
	public TokenInfo generateToken(Long userId, Boolean kind) {
		ZonedDateTime now = ZonedDateTime.now();
		Claims claims = Jwts.claims();
		TokenInfo tokenInfo;

		claims.put("userId", userId);
		if (kind == null) {
			tokenInfo = TokenInfo.builder()
				.accessToken(createAccessToken(claims, now))
				.refreshToken(createRefreshToken(claims, now))
				.build();
		} else if (kind) {
			tokenInfo = TokenInfo.builder()
				.accessToken(createAccessToken(claims, now))
				.build();
		} else {
			tokenInfo = TokenInfo.builder()
				.refreshToken(createRefreshToken(claims, now))
				.build();
		}
		return tokenInfo;
	}

	/**
	 * access token 생성
	 */
	private String createAccessToken(Claims claims, ZonedDateTime now) {
		ZonedDateTime accessTokenValidity = now.plusMinutes(accessExpiredTime);

		//Access Token 생성
		return Jwts.builder()
			.setClaims(claims)
			.setSubject("art diary")
			.setIssuedAt(Date.from(now.toInstant()))
			.setExpiration(Date.from(accessTokenValidity.toInstant()))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * refresh token 생성
	 */
	private String createRefreshToken(Claims claims, ZonedDateTime now) {
		ZonedDateTime refreshTokenValidity = now.plusMonths(refreshExpiredTime);

		//Refresh Token 생성
		return Jwts.builder()
			.setClaims(claims)
			.setSubject("art diary")
			.setIssuedAt(Date.from(now.toInstant()))
			.setExpiration(Date.from(refreshTokenValidity.toInstant()))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * Token에서 User ID 추출
	 */
	public Long getUserId(String token) {
		return parseClaims(token).get("userId", Long.class);
	}

	/**
	 * JWT 검증
	 */
	public boolean validateToken(String token, Boolean ignoreExpiration) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token: " + e.getMessage());
			if (ignoreExpiration) {
				return true;
			}
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		}
		return false;
	}

	/**
	 * JWT Claims 추출
	 */
	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}