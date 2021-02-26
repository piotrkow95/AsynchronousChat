package Messenger.services;

import Messenger.frontend.PresenceController;
import Messenger.model.MyPrincipal;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

    public void userLoggedIn(Principal principal) {
        String newUsername = null;
        if (principal instanceof MyPrincipal) {
            newUsername = usernameService.generateNewUsername();
        } else if (principal instanceof OAuth2Authentication){
            newUsername = principal.getName();
        }

        User newUser = new User(newUsername, principal.getName());
        activeUsers.put(principal.getName(), newUser);
        presenceController.publishLoginInfo(newUser);
    }

    public void userLoggedOut(String principalName) {
        User oldUser = activeUsers.remove(principalName);
        presenceController.publishLogoutInfo(oldUser);
    }

        public User getUser(String principalName) {
            return activeUsers.get(principalName);
    }

    public User getUserByName(String name) {
        return getAllActiveUsers().stream().filter(u -> u.getName().equals(name)).findFirst().get();
    }

    public Collection<User> getAllActiveUsers() {
        return activeUsers.values();
    }

}
