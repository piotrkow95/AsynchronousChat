package Messenger.services;

import Messenger.frontend.dto.SendMessageDto;
import Messenger.model.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final List<Message> allMessages = new ArrayList<>();

    public void postPublicMessage(SendMessageDto messageDto) {
        allMessages.add(new Message(messageDto.getText(), LocalDateTime.now()));
    }

    public List<Message> readAllPublicMessages() {
        return allMessages;
    }
}
