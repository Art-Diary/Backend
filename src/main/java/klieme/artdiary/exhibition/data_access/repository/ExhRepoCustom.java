package klieme.artdiary.exhibition.data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;

public interface ExhRepoCustom {
	List<Map<String, Object>> searchExhList(String searchName, List<ExhField> fieldList, ExhPrice price,
		List<ExhState> stateList, LocalDate date, Long userId);

	List<Map<String, Object>> searchExhListBySearchName(String searchName, Long userId);

	List<Map<String, Object>> getExhListForExhData();

	Map<String, Object> getExhDetailInfo(Long userId, Long exhId);
}
