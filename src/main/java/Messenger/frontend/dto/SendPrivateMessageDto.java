package Messenger.frontend.dto;

import lombok.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SendPrivateMessageDto extends SendMessageDto {
    private String recipient;
}