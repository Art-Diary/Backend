package klieme.artdiary.exhibitions.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ExhRequest {

    @NotNull
    private String exhName;

    @NotNull
    private String gallery;

    @NotNull
    private Date exhPeriodStart;

    @NotNull
    private Date exhPeriodEnd;

    @NotNull
    private String painter;

    @NotNull
    private Integer fee;

    @NotNull
    private String intro;

    @NotNull
    private String url;

    @NotNull
    private String poster;
}
