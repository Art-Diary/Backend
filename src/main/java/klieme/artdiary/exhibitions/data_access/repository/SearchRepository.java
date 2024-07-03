package klieme.artdiary.exhibitions.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import klieme.artdiary.exhibitions.data_access.entity.SearchEntity;

public interface SearchRepository extends JpaRepository<SearchEntity, Long> {

	Optional<SearchEntity> findBySearchNameAndUserId(String searchName, Long userId);
}
