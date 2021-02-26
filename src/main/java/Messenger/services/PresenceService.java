package Messenger.services;

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

    private Map<String, User> activeUsers = new HashMap<>();

    public void userLoggedIn(String sessionId) {
        String newUsername = usernameService.generateNewUsername();
        User newUser = new User(newUsername, sessionId);
        activeUsers.put(sessionId, newUser);
    }

    public void userLoggedOut(String sessionId) {
        activeUsers.remove(sessionId);
    }

    public User getUser(String sessionId) {
        return activeUsers.get(sessionId);
    }

    public Collection<User> getAllActiveUsers() {
        return activeUsers.values();
    }

}
