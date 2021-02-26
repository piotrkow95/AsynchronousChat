package Messenger.services;

import Messenger.frontend.PrivateOutboundMessageController;
import Messenger.frontend.dto.SendMessageDto;
import Messenger.frontend.dto.SendPrivateMessageDto;
import Messenger.model.Message;
import Messenger.model.MessageType;
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
    private final GifService gifService;


    public Message postPublicMessage(SendMessageDto messageDto, String principalName) {
        final Message msg = prepareMessage(messageDto, principalName);
        allMessages.add(msg);
        return msg;
    }

    public List<Message> readAllPublicMessages() {
        return allMessages;
    }

    public void postPrivateMessage(SendPrivateMessageDto messageDto, String principalName) {
        final Message msg = prepareMessage(messageDto, principalName);
        privateOutboundMessageController.publishPrivateMessage(msg);
    }

    private Message prepareMessage(SendMessageDto messageDto, String senderPrincipalName) {
        final User sender = presenceService.getUser(senderPrincipalName);
        final User recipient = (messageDto instanceof SendPrivateMessageDto)
                ? presenceService.getUserByName(((SendPrivateMessageDto) messageDto).getRecipient())
                : null;
        log.info("Received message " + messageDto.getText() + " from " +
                sender.getName() + " to " + (recipient == null ? "all" : recipient.getName()));

        String textToSend = messageDto.getText();
        MessageType messageType = MessageType.TEXT;
        if (gifService.isGifMessage(textToSend)) {
            textToSend = gifService.prepareGifMessageText(textToSend);
            messageType = MessageType.GIF;

        }

        return new Message(textToSend, LocalDateTime.now(), sender, recipient, messageType);
    }
}
