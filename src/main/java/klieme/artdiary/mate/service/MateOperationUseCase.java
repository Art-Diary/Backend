package klieme.artdiary.mate.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateOperationUseCase {

	List<MateReadUseCase.FindMateResult> addMyMateCreate(AddMyMateCreateDummy dummy);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class AddMyMateCreateDummy {
		private final Long toUserId;
	}
}
