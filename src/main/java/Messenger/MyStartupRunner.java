package Messenger;

import lombok.RequiredArgsConstructor;

import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import Messenger.services.GifService;

import java.util.List;


@Log
@RequiredArgsConstructor
@Component
public class MyStartupRunner implements CommandLineRunner {

    private final GifService gifService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Hello!");
        List<String> star_wars = gifService.searchForGifs("star wars");
    }
}
