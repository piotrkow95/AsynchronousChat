package Messenger.config;

import Messenger.services.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Log
@RequiredArgsConstructor
@Component
public class PresenceEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Principal user = event.getUser();
        String sessionId = headers.getSessionId();
        log.info("User " + sessionId + "connected.");
        presenceService.userLoggedIn(sessionId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        log.info("User " + sessionId + "disconnected.");
        presenceService.userLoggedIn(sessionId);
    }
}
