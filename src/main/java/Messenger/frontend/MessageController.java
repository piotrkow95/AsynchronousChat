package Messenger.frontend;


import Messenger.frontend.dto.SendMessageDto;
import Messenger.model.Message;
import Messenger.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/publishMessage")
    @SendTo("/topic/allMessages")
    public Message publishMessage(@Payload SendMessageDto messageDto, MessageHeaderAccessor messageHeaderAccessor) throws Exception {
        String sessionId = ((StompHeaderAccessor) messageHeaderAccessor).getSessionId();
        return messageService.postPublicMessage(messageDto, sessionId);
    }
}
