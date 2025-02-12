package klieme.artdiary.exh_data;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExhDataRequest {
	private String exhName;
	private String gallery;
	private LocalDate exhPeriodStart;
	private LocalDate exhPeriodEnd;
	private String painter;
	private Integer fee;
	private String source;
	private List<String> fieldList;
	private String url;
	private String poster;
	private String intro;
}
