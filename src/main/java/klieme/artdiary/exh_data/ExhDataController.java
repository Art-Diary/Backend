package klieme.artdiary.exh_data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/exh/data")
public class ExhDataController {
	private final ExhDataUseCase exhDataUseCase;

	@Autowired
	public ExhDataController(ExhDataUseCase exhDataUseCase) {
		this.exhDataUseCase = exhDataUseCase;
	}

	@GetMapping("")
	public String getExhDataList(Model model) {
		List<ExhEntity> list = exhDataUseCase.getExhList();

		model.addAttribute("exhs", list);
		System.out.println(list.size());
		return "exh/list";
	}

	@PostMapping("")
	public String createExhData(final ExhDataRequest params) {
		exhDataUseCase.createExhData(params);
		return "redirect:/exh/data";
	}
}