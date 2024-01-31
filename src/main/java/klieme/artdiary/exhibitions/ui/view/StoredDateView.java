package klieme.artdiary.exhibitions.ui.view;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibitions.service.ExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoredDateView {
	private final Long exhId;
	private final LocalDate visitDate;
	private final List<LocalDate> dates;

	@Builder
	public StoredDateView(ExhReadUseCase.FindStoredDateResult result) {
		this.exhId = result.getExhId();
		this.visitDate = result.getVisitDate();
		this.dates = result.getDates();
	}
}
