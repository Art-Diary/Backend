package klieme.artdiary.common.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {UserDetails}
 * Spring Security 에서 인증에 사용하기 위한 User 정보를 담은 객체.
 * public interface UserDetails를 implements하여 CustomUserDetails로 커스텀.
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final UserEntity user;
	private final Collection<GrantedAuthority> authorities;

	public static CustomUserDetails create(UserEntity user) {
		List<String> roles = new ArrayList<>();

		roles.add(user.getRoleType());
		return new CustomUserDetails(user,
			roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getUserId().toString();
	}

	@Override
	public String getUsername() {
		return user.getUserId().toString();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public UserEntity getUserEntity() {
		return user;
	}
}
