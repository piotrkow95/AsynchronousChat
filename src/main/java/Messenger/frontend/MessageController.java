package Messenger.frontend;


import Messenger.frontend.dto.SendMessageDto;
import Messenger.frontend.dto.SendPrivateMessageDto;
import Messenger.model.Message;
import Messenger.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/publishPublicMessage")
    @SendTo("/topic/allMessages")
    public Message publishPublicMessage(@Payload SendMessageDto messageDto,
                                        Principal principal) {
        return messageService.postPublicMessage(messageDto, principal.getName());
    }

    @MessageMapping("/publishPrivateMessage")
    public void publishPrivateMessage(@Payload SendPrivateMessageDto messageDto,
                                      Principal principal) {
        messageService.postPrivateMessage(messageDto, principal.getName());
    }

}
