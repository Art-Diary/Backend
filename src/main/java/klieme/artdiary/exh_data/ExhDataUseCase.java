package klieme.artdiary.exh_data;

import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;

public interface ExhDataUseCase {

	List<ExhEntity> getExhList();

	void createExhData(final ExhDataRequest params);
}
