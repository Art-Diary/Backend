package klieme.artdiary.exhibition.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;

@Repository
public interface RegExhRepository extends JpaRepository<RegExhEntity, Long>, RegExhCustom {
	List<RegExhEntity> findByUserId(Long userId);
}
