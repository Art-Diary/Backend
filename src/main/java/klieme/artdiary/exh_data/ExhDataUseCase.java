package klieme.artdiary.exh_data;

import java.util.List;

public interface ExhDataUseCase {

	List<ExhDataResponse> getExhList();

	void createExhData(final ExhDataRequest params);
}
