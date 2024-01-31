package klieme.artdiary.exhibitions.ui.request_body;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
public class AddSoloExhRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate visitDate;

}
