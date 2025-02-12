package klieme.artdiary.exh_data;

import static klieme.artdiary.common.FormatDate.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.exhibition.data_access.entity.CategoryEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkEntity;
import klieme.artdiary.exhibition.data_access.entity.ExhCategoryLinkId;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.CategoryRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhCategoryLinkRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;

@Service
public class ExhDataService implements ExhDataUseCase {

	private final ExhRepository exhRepository;
	private final CategoryRepository categoryRepository;
	private final ExhCategoryLinkRepository exhCategoryLinkRepository;

	@Autowired
	public ExhDataService(ExhRepository exhRepository, CategoryRepository categoryRepository,
		ExhCategoryLinkRepository exhCategoryLinkRepository) {
		this.exhRepository = exhRepository;
		this.categoryRepository = categoryRepository;
		this.exhCategoryLinkRepository = exhCategoryLinkRepository;
	}

	@Override
	public List<ExhDataResponse> getExhList() {
		List<Map<String, Object>> list = exhRepository.getExhListForExhData();
		List<ExhDataResponse> results = new ArrayList<>();

		for (Map<String, Object> value : list) {
			ExhEntity exh = (ExhEntity)value.get("exhibition");
			String category = (String)value.get("category");
			System.out.println(category);

			results.add(ExhDataResponse.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.source(exh.getSource())
				.gallery(exh.getGallery())
				.exhPeriodStart(changeDateFormat(exh.getExhPeriodStart()))
				.exhPeriodEnd(changeDateFormat(exh.getExhPeriodEnd()))
				.painter(exh.getPainter())
				.fee(exh.getFee())
				.art(category)
				.url(exh.getUrl())
				.poster(exh.getPoster())
				.intro(exh.getIntro())
				.build());
		}
		return results;
	}

	@Override
	@Transactional
	public void createExhData(final ExhDataRequest params) {
		// 포스터 다운로드
		ExhEntity newExh = ExhEntity.builder()
			.exhName(params.getExhName())
			.source(params.getSource())
			.gallery(params.getGallery())
			.exhPeriodStart(params.getExhPeriodStart())
			.exhPeriodEnd(params.getExhPeriodEnd())
			.painter(params.getPainter())
			.fee(params.getFee())
			.url(params.getUrl())
			.poster(params.getPoster())
			.intro(params.getIntro())
			.build();

		exhRepository.save(newExh);

		List<CategoryEntity> categoryList = categoryRepository.findAll();

		for (String field : params.getFieldList()) {
			CategoryEntity category = categoryList.stream()
				.filter(c -> field.equals(c.getName()))
				.findAny()
				.orElse(null);

			if (category == null) {
				continue;
			}
			exhCategoryLinkRepository.save(ExhCategoryLinkEntity.builder()
				.exhCategoryLinkId(ExhCategoryLinkId.builder()
					.exhId(newExh.getExhId())
					.categoryId(category.getCategoryId())
					.build())
				.build());
		}
	}
}
