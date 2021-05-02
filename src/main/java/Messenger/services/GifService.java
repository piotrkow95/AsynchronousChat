package Messenger.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Messenger.exceptions.GifException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log
public class GifService {
    public boolean isGifMessage(String textToSend) {
        return textToSend != null && textToSend.startsWith("::");
    }

    public String prepareGifMessageText(String textToSend) throws GifException {
        if (!isGifMessage(textToSend)) {
            return textToSend;
        }
        String searchText = textToSend.replaceFirst("::", "");

        return fetchGif(searchText);
    }

    private String fetchGif(String searchText) throws GifException {
        try {
            Map<String, Map<String, String>> result = new RestTemplate().getForObject("https://api.giphy.com/v1/gifs/random?" +
                    "api_key=4AWXpY3c6uLL8uH9dBWS8Xku4KFcQ2hE&" +
                    "tag=" + searchText + "&" +
                    "rating=g", Map.class);
            String mp4Link = result.get("data").get("image_mp4_url");
            return mp4Link;
        } catch (Exception e) {
            throw new GifException(e);
        }
    }
}
