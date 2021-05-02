package Messenger.services;


import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SessionScope
public class UsernameService {
    private final Random random = new Random();
    private final Map<String, String> namesImagesMap = new HashMap<>();
    private final List<String> availableUsernames = new LinkedList<>();
    private int lastGeneratedIndex = 0;

    public UsernameService() {
        namesImagesMap.put("Mysz", "/avatars/001-mouse.png");
        namesImagesMap.put("Krowa", "/avatars/002-cow.png");
        namesImagesMap.put("Kangur", "/avatars/003-kangaroo.png");
        namesImagesMap.put("Niedźwiedź", "/avatars/004-bear.png");
        namesImagesMap.put("Flaming", "/avatars/005-flamingo.png");
        namesImagesMap.put("Lis", "/avatars/006-fox.png");
        namesImagesMap.put("Nietoperz", "/avatars/007-bat.png");
        namesImagesMap.put("Krab", "/avatars/008-crab.png");
        namesImagesMap.put("Lew", "/avatars/009-lion.png");
        namesImagesMap.put("Żaba", "/avatars/010-frog.png");
        namesImagesMap.put("Koala", "/avatars/012-koala.png");
        namesImagesMap.put("Tygrys", "/avatars/013-tiger.png");
        namesImagesMap.put("Wiewiórka", "/avatars/015-squirrel.png");
        namesImagesMap.put("Lama", "/avatars/022-llama.png");
    }

    public Map.Entry<String, String> generateNewUsername() {
        if (availableUsernames.size() == 0) {
            lastGeneratedIndex++;
            availableUsernames.addAll(namesImagesMap.keySet());
        }

        int randomIndex = random.nextInt(availableUsernames.size());
        String name = availableUsernames.remove(randomIndex);
        String avatarUrl = namesImagesMap.get(name);
        name += lastGeneratedIndex == 1 ? "" : (" " + lastGeneratedIndex);
        return Map.entry(name, avatarUrl);
    }
}
