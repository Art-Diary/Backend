package klieme.artdiary.exhibition.data_access.repository;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;

public interface ExhRepoCustom {
	List<ExhEntity> searchExhList(String searchName, List<ExhField> fieldList, ExhPrice price, List<ExhState> stateList,
		LocalDate date);
}
