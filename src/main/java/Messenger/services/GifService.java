package Messenger.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Messenger.exceptions.GifException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<String> searchForGifs(String textToSearch) throws GifException {
        String searchText = textToSearch.replaceFirst("::", "");
        try {
            Map<String, List<Map<String, Map<String, Map<String, String>>>>> result =
                    new RestTemplate().getForObject("https://api.giphy.com/v1/gifs/search?" +
                            "api_key=4AWXpY3c6uLL8uH9dBWS8Xku4KFcQ2hE&" +
                            "q=" + searchText + "&" +
                            "rating=g", Map.class);
            List<String> mp4s = result.get("data").stream()
                    .map(gif -> gif.get("images"))
                    .map(g -> g.get("original_mp4").get("mp4"))
                    .collect(Collectors.toList());
            return mp4s;
        } catch (Exception e) {
            throw new GifException(e);
        }
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
