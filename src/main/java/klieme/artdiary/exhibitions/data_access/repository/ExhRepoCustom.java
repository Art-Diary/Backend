package klieme.artdiary.exhibitions.data_access.repository;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.enums.ExhField;
import klieme.artdiary.exhibitions.enums.ExhPrice;
import klieme.artdiary.exhibitions.enums.ExhState;

public interface ExhRepoCustom {
	List<ExhEntity> searchExhList(String searchName, List<ExhField> fieldList, ExhPrice price, List<ExhState> stateList,
		LocalDate date);
}
