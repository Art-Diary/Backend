package klieme.artdiary.myexhs.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.ArtDiaryException;
import klieme.artdiary.common.MessageType;
import klieme.artdiary.common.UserIdFilter;
import klieme.artdiary.exhibitions.data_access.entity.ExhEntity;
import klieme.artdiary.exhibitions.data_access.entity.UserExhEntity;
import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.gatherings.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringExhEntity;
import klieme.artdiary.gatherings.data_access.entity.GatheringMateEntity;
import klieme.artdiary.gatherings.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringExhRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringMateRepository;
import klieme.artdiary.gatherings.data_access.repository.GatheringRepository;
import klieme.artdiary.mydiarys.data_access.entity.MydiaryEntity;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;

@Service
public class MyExhsService implements MyExhsReadUseCase, MyExhsOperationUseCase {
	private final GatheringMateRepository gatheringMateRepository;
	private final GatheringRepository gatheringRepository;
	private final GatheringExhRepository gatheringExhRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final UserExhRepository userExhRepository;
	private final ExhRepository exhRepository;
	private final MydiaryRepository mydiaryRepository;

	@Autowired
	public MyExhsService(GatheringMateRepository gatheringMateRepository, GatheringRepository gatheringRepository,
		GatheringExhRepository gatheringExhRepository, GatheringDiaryRepository gatheringDiaryRepository,
		UserExhRepository userExhRepository, ExhRepository exhRepository, MydiaryRepository mydiaryRepository) {
		this.gatheringMateRepository = gatheringMateRepository;
		this.gatheringRepository = gatheringRepository;
		this.gatheringExhRepository = gatheringExhRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.userExhRepository = userExhRepository;
		this.exhRepository = exhRepository;
		this.mydiaryRepository = mydiaryRepository;
	}

	@Override
	public List<MyExhsReadUseCase.FindMyExhsResult> getMyExhsList() {
		Long userId = getUserId();

		List<MyExhsReadUseCase.FindMyExhsResult> myExhs = new ArrayList<>(); //중복된 전시회 리스트 없고, rate 평균 계산된 상태
		List<MyExhsReadUseCase.FindMyExhsResult> myAllExhs = new ArrayList<>(); //모든 전시회 받아옴.
		// List<GatheringReadUseCase.FindGatheringResult> gatherings = new ArrayList<>();
		Map<Long, Integer> checkRate = new HashMap<>();
		// Map<Long, Double> realRate = new HashMap<Long, Double>();//지금까지의 총 평점 합 -> 아마 필요 없을 듯

		//혼자 갔다온 전시회 있는지 확인
		List<UserExhEntity> soloExhEntities = userExhRepository.findByUserId(userId);

		//userexh의 exhId로 exhibition에서 전시회 정보 확인(전시회이름, 포스터), userExhId로 SoloDiary에서 그 전시회에 대한 기록 정보 확인(평균)
		//확인 후 myAllExhs에 저장
		for (UserExhEntity Entity : soloExhEntities) {
			//혼자갔다온 전시회 정보 가져오기(exhId,exhName,poster)
			ExhEntity exhEntity = exhRepository.findByExhId(Entity.getExhId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

			//혼자갔다온 전시회에 대한 기록 가져오기 (rate)
			List<MydiaryEntity> AllmydiaryEntity = mydiaryRepository.findByUserExhId(Entity.getUserExhId());

			//한 userExhId에 대한 기록이 여러 개일 경우, for문 돌려서 myAllExhs에 저장. -> 근데 굳이 list로 받을 필요가 있나?
			for (MydiaryEntity tmp : AllmydiaryEntity) {
				myAllExhs.add(
					MyExhsReadUseCase.FindMyExhsResult.findMyExhs(exhEntity, tmp.getRate()));//mydiaryEntity.getRate());
			}
		}

		//모임에서 갔다온 전시회 (중복 확인 필요)
		//모임있는지 확인
		List<GatheringMateEntity> gEntities = gatheringMateRepository.findByGatheringMateIdUserId(userId);

		//모임있을시, userId도 확인하고(내기록만 가져와야하니까), 한 모임의 한 전시를
		for (GatheringMateEntity gEntity : gEntities) {
			List<GatheringExhEntity> gatheringExhEntities = gatheringExhRepository.findByGatherId(
				gEntity.getGatheringMateId().getGatherId());

			//gatherId로 gatherExh에서 exhId 확인, exhId로 exhibitions에서 전시회 정보 확인(id,exhname,poster)
			//gatherId로 gatherExh에서 gatherExhId 확인 gatherExhId로 gatheringdiary에서 rate 확인,
			for (GatheringExhEntity Entity : gatheringExhEntities) {
				//모임에서 갔다 온 전시회 정보 가져오기(exhId,exhName,poster)
				ExhEntity exhEntity = exhRepository.findByExhId(
					Entity.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

				//모임에서 갔다 온 전시회에 대한 기록 가져오기 (rate)
				List<GatheringDiaryEntity> AllgatheringDiaryEntity = gatheringDiaryRepository.findByGatheringExhId(
					Entity.getGatheringExhId());

				for (GatheringDiaryEntity gatheringDiaryEntity : AllgatheringDiaryEntity) {
					myAllExhs.add(
						MyExhsReadUseCase.FindMyExhsResult.findMyExhs(exhEntity, gatheringDiaryEntity.getRate()));
				}
			}
		}

		//중복되는 전시회 평점 총합으로 계산해서 myexhs에 저장
		for (MyExhsReadUseCase.FindMyExhsResult tmpresult : myAllExhs) {

			if (myExhs.isEmpty()) {//myExhs에 아무것도 없을때
				myExhs.add(tmpresult);
				checkRate.put(tmpresult.getExhId(), 1);
			} else {
				int index = 0;
				boolean checkEmpty = true;
				for (MyExhsReadUseCase.FindMyExhsResult tmpList : myExhs) {//
					if (tmpList.equalsExhId(tmpresult)) {
						checkRate.replace(tmpresult.getExhId(), checkRate.get(tmpresult.getExhId()) + 1);

						myExhs.set(index,
							MyExhsReadUseCase.FindMyExhsResult.UpdateMyrate(tmpList.getExhId(), tmpList.getExhName(),
								tmpList.getPoster(), tmpList.getRate() + tmpresult.getRate()));
						checkEmpty = false;

					}

					index = index + 1;
				}

				if (checkEmpty) {
					checkRate.put(tmpresult.getExhId(), 1);
					myExhs.add(tmpresult);
				}
			}
		}

		// 전시회 마다 평점 계산
		int index = 0;
		for (MyExhsReadUseCase.FindMyExhsResult result : myExhs) {
			double resultRate = result.getRate() / checkRate.get(result.getExhId());
			myExhs.set(index, MyExhsReadUseCase.FindMyExhsResult.UpdateMyrate(result.getExhId(), result.getExhName(),
				result.getPoster(), resultRate));
			index++;
		}

		return myExhs;
	}

	@Override
	public List<FindMyStoredDateResult> getStoredDateOfExhs(MyStoredDateFindQuery query) {
		Long userId = getUserId();
		List<FindMyStoredDateResult> results = new ArrayList<>();

		// 전시회 아이디 검증
		ExhEntity exhEntity = exhRepository.findByExhId(query.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 전시회에 대한 개인의 일정 -> userExh 테이블
		List<UserExhEntity> userExhEntities = userExhRepository.findByUserIdAndExhId(userId, exhEntity.getExhId());
		if (!userExhEntities.isEmpty()) {
			List<LocalDate> dates = new ArrayList<>();
			for (UserExhEntity userExh : userExhEntities) {
				dates.add(userExh.getVisitDate());
			}
			results.add(FindMyStoredDateResult.findByMyStoredDateSolo(userExhEntities.get(0), dates));
		}
		// 자신이 속한 모임에 대한 한 전시회에 대한 일정 -> gatherMate, gatherExh 테이블
		// 자신이 속한 모임 목록
		List<GatheringMateEntity> gatheringMateEntities = gatheringMateRepository.findByGatheringMateIdUserId(userId);
		// 전시회에 대한 모임의 일정
		for (GatheringMateEntity gatheringMate : gatheringMateEntities) {
			List<GatheringExhEntity> gatheringExhEntities = gatheringExhRepository.findByGatherIdAndExhId(
				gatheringMate.getGatheringMateId().getGatherId(), exhEntity.getExhId());
			if (!gatheringExhEntities.isEmpty()) {
				List<LocalDate> dates = new ArrayList<>();
				for (GatheringExhEntity gatheringExh : gatheringExhEntities) {
					dates.add(gatheringExh.getVisitDate());
				}
				GatheringEntity gathering = gatheringRepository.findByGatherId(
						gatheringExhEntities.get(0).getGatherId())
					.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
				results.add(
					FindMyStoredDateResult.findByMyStoredDateGather(gatheringExhEntities.get(0), gathering, dates));
			}
		}
		return results;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}


