package Messenger.frontend.dto;


import Messenger.model.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendMessageDto {
    private String text;
    private MessageType type;
}
