package klieme.artdiary.mydiarys.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;

@Repository
public interface MydiaryRepository extends JpaRepository<MydiaryEntity, Long> {
}
