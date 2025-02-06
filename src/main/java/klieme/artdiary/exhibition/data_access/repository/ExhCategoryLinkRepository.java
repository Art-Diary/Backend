package klieme.artdiary.exhibition.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkId;

@Repository
public interface ExhCategoryLinkRepository extends JpaRepository<ExhCategoryLinkEntity, ExhCategoryLinkId> {
}
