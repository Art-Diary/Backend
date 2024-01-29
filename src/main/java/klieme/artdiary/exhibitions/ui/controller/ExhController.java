package klieme.artdiary.exhibitions.ui.controller;

import jakarta.validation.Valid;
import klieme.artdiary.exhibitions.service.ExhOperationUseCase;
import klieme.artdiary.exhibitions.ui.request_body.ExhRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/exhibitions")
public class ExhController {

    private final ExhOperationUseCase exhOperationUseCase;

    @Autowired
    public ExhController(ExhOperationUseCase exhOperationUseCase) {
        this.exhOperationUseCase = exhOperationUseCase;
    }

    @GetMapping("/hello")
    public void helloPrint() {
        System.out.println("hello");
    }

    @PostMapping("")
    public void createDummyDate(@Valid @RequestBody ExhRequest exhRequest){

        System.out.println("Test");

        var command = ExhOperationUseCase.ExhDummyCreateCommand.builder()
                .exhName(exhRequest.getExhName())
                .gallery(exhRequest.getGallery())
                .exhPeriodStart(exhRequest.getExhPeriodStart())
                .exhPeriodEnd(exhRequest.getExhPeriodEnd())
                .painter(exhRequest.getPainter())
                .fee(exhRequest.getFee())
                .intro(exhRequest.getIntro())
                .url(exhRequest.getUrl())
                .poster(exhRequest.getPoster())
                .build();

        System.out.println(exhOperationUseCase.createDummy(command));
    }
}
