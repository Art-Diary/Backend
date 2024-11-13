// package klieme.artdiary.gathering.data_access.repository;
//
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;
//
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
//
// import klieme.artdiary.gathering.data_access.entity.GatheringExhEntity;
//
// @Repository
// public interface GatheringExhRepository extends JpaRepository<GatheringExhEntity, Long>, GatheringExhRepoCustom {
// 	Optional<GatheringExhEntity> findByGatherIdAndExhIdAndVisitDate(Long gatherId, Long exhId, LocalDate visitDate);
//
// 	List<GatheringExhEntity> findByGatherId(Long gatherId);
//
// 	List<GatheringExhEntity> findByExhId(Long exhId);
//
// 	List<GatheringExhEntity> findByGatherIdAndExhId(Long gatherId, Long exhId);
//
// 	Optional<GatheringExhEntity> findByGatherExhId(Long gatherExhId);
//
// 	List<GatheringExhEntity> findByGatherIdAndVisitDateBetween(Long gatherId, LocalDate visitDateStart,
// 		LocalDate visitDateEnd);
// }
