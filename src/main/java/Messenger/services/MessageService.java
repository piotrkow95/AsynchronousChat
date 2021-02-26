package Messenger.services;

import Messenger.frontend.dto.SendMessageDto;
import Messenger.model.Message;
import Messenger.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final List<Message> allMessages = new ArrayList<>();

    private final UsernameService usernameService;

    public Message postPublicMessage(SendMessageDto messageDto) {
        Message msg = new Message(messageDto.getText(), LocalDateTime.now(), new User(usernameService.getUsername()));
        allMessages.add(msg);
        return msg;
    }

    public List<Message> readAllPublicMessages() {
        return allMessages;
    }
}
