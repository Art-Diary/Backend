package klieme.artdiary.exhibition.ui.request_body;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchContentsRequest {
	@NotNull
	public String searchContent;

	//@DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
	public LocalDateTime searchTime;
}
