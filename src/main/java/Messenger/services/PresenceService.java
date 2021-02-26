package Messenger.services;

import Messenger.frontend.PresenceController;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log
public class PresenceService {
    private final UsernameService usernameService;
    private final PresenceController presenceController;

    private Map<String, User> activeUsers = new HashMap<>();

    public void userLoggedIn(String sessionId) {
        String newUsername = usernameService.generateNewUsername();
        User newUser = new User(newUsername, sessionId);
        activeUsers.put(sessionId, newUser);
    }

    public void userLoggedOut(String sessionId) {
        User oldUser = activeUsers.remove(sessionId);
        presenceController.publishLogoutInfo(oldUser);
    }

    public User getUser(String sessionId) {
        return activeUsers.get(sessionId);
    }

    public User getUserByName(String name) {
        return getAllActiveUsers().stream().filter(u -> u.getName().equals(name)).findFirst().get();
    }

    public Collection<User> getAllActiveUsers() {
        return activeUsers.values();
    }

}
