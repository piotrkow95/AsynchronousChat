package Messenger.services;

import Messenger.frontend.PrivateOutboundMessageController;
import Messenger.frontend.dto.SendMessageDto;
import Messenger.frontend.dto.SendPrivateMessageDto;
import Messenger.model.Conversation;
import Messenger.model.Message;
import Messenger.model.MessageType;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log
public class MessageService {
    private final List<Message> allMessages = new ArrayList<>();
    private final PresenceService presenceService;
    private final PrivateOutboundMessageController privateOutboundMessageController;

    private final Map<Conversation, List<Message>> privateMessages = new HashMap<>();

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
        Conversation conversation = new Conversation(Set.of(
                msg.getSender().getPrincipalName(),
                msg.getRecipient().getPrincipalName()));
        privateMessages.computeIfAbsent(conversation, c -> new ArrayList<>());
        privateMessages.computeIfPresent(conversation, (c, messageList) -> {
            messageList.add(msg);
            return messageList;
        });

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
        MessageType messageType = messageDto.getType();
        return new Message(textToSend, LocalDateTime.now(), sender, recipient, messageType);
    }

    public void sendRecap(Principal principal) {
        // powinno być rozwiązane inaczej - sleep to zła praktyka
        // zamiast tego analogicznie jak presence (PrecenceRestController + javascript w connect() do jego obsługi
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final User recipient = presenceService.getUser(principal.getName());
            for (Message msg : allMessages) {
                privateOutboundMessageController.publishOldPublicMessage(recipient, msg);
            }
        }).start();
    }

    public List<Message> getConversationMessages(@Nullable String displayedUserName, String principalName) {
        if (displayedUserName == null) {
            return readAllPublicMessages();
        }
        User user1 = presenceService.getUserByName(displayedUserName);
        User user2 = presenceService.getUser(principalName);

        Conversation conversation = new Conversation(Set.of(user1.getPrincipalName(), user2.getPrincipalName()));
        return privateMessages.get(conversation);
    }
}
