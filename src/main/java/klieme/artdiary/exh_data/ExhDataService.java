package klieme.artdiary.exh_data;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;

@Service
public class ExhDataService implements ExhDataUseCase {

	private final ExhRepository exhRepository;

	@Autowired
	public ExhDataService(ExhRepository exhRepository) {
		this.exhRepository = exhRepository;
	}

	@Override
	public List<ExhEntity> getExhList() {
		List<ExhEntity> list = exhRepository.findAll();
		Collections.reverse(list);
		return list;
	}

	@Override
	@Transactional
	public void createExhData(final ExhDataRequest params) {
		// 포스터 다운로드
		exhRepository.save(ExhEntity.builder()
			.exhName(params.getExhName())
			.source(params.getSource())
			.gallery(params.getGallery())
			.exhPeriodStart(params.getExhPeriodStart())
			.exhPeriodEnd(params.getExhPeriodEnd())
			.painter(params.getPainter())
			.fee(params.getFee())
			.art(params.getArt())
			.url(params.getUrl())
			.poster(params.getPoster())
			.intro(params.getIntro())
			.build());
	}
}
