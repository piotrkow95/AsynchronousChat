package Messenger.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private final String text;
    private final LocalDateTime timestamp;

}
