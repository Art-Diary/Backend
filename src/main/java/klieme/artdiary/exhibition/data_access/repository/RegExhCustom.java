package klieme.artdiary.exhibition.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;

public interface RegExhCustom {
	List<Map<String, Object>> getRegExhListByAdmin();

	List<RegExhEntity> getRegExhListByUser(Long userId);

	Map<String, Object> getRegExhWithExhByAdmin(Long regExhId);
}
