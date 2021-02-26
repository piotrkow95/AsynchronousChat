package Messenger.frontend.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendMessageDto {
    private String text;
    private String username;
}
