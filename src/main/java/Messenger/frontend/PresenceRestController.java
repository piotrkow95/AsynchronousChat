package Messenger.frontend;

import Messenger.model.User;
import Messenger.services.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

@Log
@RequiredArgsConstructor
@RestController
public class PresenceRestController {

    private final PresenceService presenceService;

    @GetMapping(path = "/hello")
    public String hello() {
        return "Hello!";
    }

    @GetMapping(path = "/allActiveUsers")
    public Collection<User> fetchAllActiveUsers(Principal principal) {
        Collection<User> allActiveUsers = presenceService.getAllActiveUsers();
        log.info("Returning all active users: " + allActiveUsers);
        return allActiveUsers;
    }

}