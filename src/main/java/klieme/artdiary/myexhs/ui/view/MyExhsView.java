package klieme.artdiary.myexhs.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.myexhs.service.MyExhsReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyExhsView {
	private final Long exhId;
	private final String exhName;
	private final String poster;
	private final Double rate;

	@Builder
	public MyExhsView(MyExhsReadUseCase.FindMyExhsResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.poster = result.getPoster();
		this.rate = result.getRate();
	}

}
