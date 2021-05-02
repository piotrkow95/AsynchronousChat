package Messenger.frontend;


import Messenger.frontend.dto.PresenceEventDto;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static Messenger.frontend.dto.PresenceEventDto.PresenceEventType.USER_LOGGED_IN;
import static Messenger.frontend.dto.PresenceEventDto.PresenceEventType.USER_LOGGED_OUT;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publishLoginInfo(User newUser) {
        PresenceEventDto dto = new PresenceEventDto(newUser, USER_LOGGED_IN);
        simpMessagingTemplate.convertAndSend("/topic/allLogins", dto);
    }

    public void publishLogoutInfo(User newUser) {
        PresenceEventDto dto = new PresenceEventDto(newUser, USER_LOGGED_OUT);
        simpMessagingTemplate.convertAndSend("/topic/allLogins", dto);
    }

}
