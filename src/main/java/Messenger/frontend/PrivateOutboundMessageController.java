package Messenger.frontend;

import Messenger.model.Message;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PrivateOutboundMessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publicPrivateMessage(Message msg, User sender, User recipient) {
        simpMessagingTemplate.convertAndSendToUser(sender.getSessionId(), "/topic/privateMessages", msg);
        simpMessagingTemplate.convertAndSendToUser(recipient.getSessionId(), "/topic/privateMessages", msg);
    }

}