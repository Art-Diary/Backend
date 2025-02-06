package klieme.artdiary.exh_data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhDataResponse {
	private Long exhId;
	private String exhName;
	private String gallery;
	private String exhPeriodStart;
	private String exhPeriodEnd;
	private String poster;
	private String painter;
	private Integer fee;
	private String intro;
	private String url;
	private String art;
	private String source;
}
