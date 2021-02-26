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


@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/publishPublicMessage")
    @SendTo("/topic/allMessages")
    public Message publishPublicMessage(@Payload SendMessageDto messageDto,
                                        MessageHeaderAccessor messageHeaderAccessor) {
        String sessionId = ((StompHeaderAccessor) messageHeaderAccessor).getSessionId();
        return messageService.postPublicMessage(messageDto, sessionId);
    }

    @MessageMapping("/publishPrivateMessage")
    public void publishPrivateMessage(@Payload SendPrivateMessageDto messageDto,
                                      MessageHeaderAccessor messageHeaderAccessor) {
        String sessionId = ((StompHeaderAccessor) messageHeaderAccessor).getSessionId();
        messageService.postPrivateMessage(messageDto, sessionId);
    }

}
