package Messenger.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import Messenger.services.MessageService;
import Messenger.services.PresenceService;

import java.security.Principal;

@Log
@RequiredArgsConstructor
@Component
public class WebsocketEventListener {

    private final PresenceService presenceService;
    private final MessageService messageService;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        // guests = sessionID (from UserInterceptor)
        // github users = login (from oauth)
        String principalName = event.getUser().getName();
        log.info("User " + principalName + " connected.");
        presenceService.userLoggedIn(event.getUser());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String principalName = event.getUser() == null ? null : event.getUser().getName();
        log.info("User: " + principalName + " connected.");
        presenceService.userLoggedOut(principalName);
    }

    @EventListener
    public void handleTopicSubscription(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Principal principal = event.getUser();
        String destination = headers.getDestination();
        switch (destination) {
            case "/topic/allMessages":
                messageService.sendRecap(principal);
                break;
        }
    }
}
