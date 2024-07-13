package klieme.artdiary.solo.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.service.MyExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyExhView {
	private final Long exhId;
	private final String exhName;
	private final String poster;
	private final Double rate;

	@Builder
	public MyExhView(MyExhReadUseCase.FindMyExhsResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.poster = result.getPoster();
		this.rate = result.getRate();
	}

}
