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

    public void postPublicMessage(SendMessageDto messageDto) {
        allMessages.add(new Message(messageDto.getText(), LocalDateTime.now(), new User(usernameService.getUsername())));
    }

    public List<Message> readAllPublicMessages() {
        return allMessages;
    }
}
