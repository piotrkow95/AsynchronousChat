package Messenger.frontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import Messenger.exceptions.GifException;
import Messenger.services.GifService;

import java.util.Collections;
import java.util.List;

@Log
@RequiredArgsConstructor
@RestController
public class GifRestController {

    private final GifService gifService;

    @GetMapping(path = "/searchGifs")
    public List<String> fetchAllActiveUsers(@RequestParam String search) {
        try {
            return gifService.searchForGifs(search);
        } catch (GifException e) {
            return Collections.emptyList();
        }
    }

}
