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

    public void publishPrivateMessage(Message msg) {
        simpMessagingTemplate.convertAndSendToUser(msg.getSender().getPrincipalName(), "/topic/privateMessages", msg);
        simpMessagingTemplate.convertAndSendToUser(msg.getRecipient().getPrincipalName(), "/topic/privateMessages", msg);

    }
}