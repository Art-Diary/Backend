package klieme.artdiary.users.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.users.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAlarmView {
	private final Boolean alarm1;
	private final Boolean alarm2;
	private final Boolean alarm3;

	@Builder
	public UserAlarmView(UserReadUseCase.FindAlarmResult result) {
		this.alarm1 = result.getAlarm1();
		this.alarm2 = result.getAlarm2();
		this.alarm3 = result.getAlarm3();
	}
}
