package klieme.artdiary.exhibition.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.service.RegExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegExhListView {
	private final Long no;
	private final Long regExhId;
	private final String regExhName;
	private final String regNickName;
	private final String regDate;
	private final String regState;

	@Builder
	public RegExhListView(RegExhReadUseCase.FindRegExhListResult result) {
		this.no = result.getNo();
		this.regExhId = result.getRegExhId();
		this.regExhName = result.getRegExhName();
		this.regNickName = result.getRegNickName();
		this.regDate = result.getRegDate();
		this.regState = result.getRegState();
	}
}
