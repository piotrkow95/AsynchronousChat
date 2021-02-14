package Messenger.services;


import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@SessionScope
public class UsernameService {
    private static final Random RANDOM = new Random();
    private static final String[] NAMES = {"Cat", "Dog", "Hamster", "Horse", "Cow", "Chicken", "Indyk", "Pig", "Elephant",
            "Giraffe", "Tiger", "Lion", "Ant"};
    private static final List<String> availableUsernames = new LinkedList<>();
    private static int lastGeneratedIndex = 0;

    private String username = null;

    public String getUsername() {
        if (username == null) {
            username = generateNewUsername();
        }
        return username;
    }

    private String generateNewUsername() {
        if (availableUsernames.size() == 0) {
            lastGeneratedIndex++;
            availableUsernames.addAll(Arrays.stream(NAMES)
            .map(n -> n.substring(0,1).toUpperCase() + n.substring(1) + (lastGeneratedIndex == 1 ? "" : (" " + lastGeneratedIndex)))
                    .collect(Collectors.toSet()));
        }

        int randomIndex = RANDOM.nextInt(availableUsernames.size());
        String name = availableUsernames.remove(randomIndex);
        return name;
    }
}
