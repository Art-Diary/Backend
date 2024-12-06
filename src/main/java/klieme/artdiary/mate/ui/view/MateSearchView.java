package klieme.artdiary.mate.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.mate.service.MateReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateSearchView {
	private final List<MateReadUseCase.FindMateResult> alreadyMate;
	private final List<MateReadUseCase.FindMateResult> notMate;

	@Builder
	public MateSearchView(MateReadUseCase.FindIsMateResult result) {
		this.alreadyMate = result.getAlreadyMate();
		this.notMate = result.getNotMate();
	}
}
