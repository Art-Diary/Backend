package klieme.artdiary.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.user.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAlarmView {
	private final Boolean alarm;

	@Builder
	public UserAlarmView(UserReadUseCase.FindAlarmResult result) {
		this.alarm = result.getAlarm();
	}
}
