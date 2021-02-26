package Messenger.services;

import Messenger.frontend.PrivateOutboundMessageController;
import Messenger.frontend.dto.SendMessageDto;
import Messenger.frontend.dto.SendPrivateMessageDto;
import Messenger.model.Message;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class MessageService {
    private final List<Message> allMessages = new ArrayList<>();
    private final PresenceService presenceService;
    private final PrivateOutboundMessageController privateOutboundMessageController;

    public Message postPublicMessage(SendMessageDto messageDto, String sessionId) {
        User sender = presenceService.getUser(sessionId);
        Message msg = new Message(messageDto.getText(), LocalDateTime.now(), sender, null);
        allMessages.add(msg);
        return msg;
    }

    public List<Message> readAllPublicMessages() {
        return allMessages;
    }

    public void postPrivateMessage(SendPrivateMessageDto messageDto, String sessionId) {
        final User sender = presenceService.getUser(sessionId);
        final User recipient = presenceService.getUserByName(messageDto.getRecipient());
        log.info("Received private message " + messageDto.getText() + " from " +
                sender.getName() + " to " + messageDto.getRecipient());

        Message msg = new Message("\uD83D\uDD12" + messageDto.getText(), LocalDateTime.now(), sender, recipient);
        privateOutboundMessageController.publicPrivateMessage(msg, sender, recipient);
    }
}
