package klieme.artdiary.gatherings.data_access.repository;

import java.util.List;

import com.querydsl.core.Tuple;

public interface GatheringExhRepoCustom {
	List<Tuple> getVisitExhWithUser();
}
