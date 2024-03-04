package klieme.artdiary.users.ui.request_body;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class DeleteReasonRequest {
	String reason;
}
