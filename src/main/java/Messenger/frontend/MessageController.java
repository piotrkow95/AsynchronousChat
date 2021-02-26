package Messenger.frontend;


import Messenger.frontend.dto.SendMessageDto;
import Messenger.model.Message;
import Messenger.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/publishMessage")
    @SendTo("/topic/allMessages")
    public Message publishMessage(SendMessageDto messageDto) throws Exception {
        return messageService.postPublicMessage(messageDto);

    }
}
