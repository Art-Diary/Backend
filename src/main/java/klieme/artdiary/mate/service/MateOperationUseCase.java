package klieme.artdiary.mate.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateOperationUseCase {

	List<MateReadUseCase.FindMateResult> addMyMateCreate(AddMyMateCreateDummy dummy) throws IOException;

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class AddMyMateCreateDummy {
		private final Long toUserId;
	}
}
