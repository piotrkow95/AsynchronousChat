package Messenger.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Message {

    private static DateTimeFormatter FORMATTER_TIMESTAMP = DateTimeFormatter.ofPattern("hh:mm:ss");

    private final String text;
    private final LocalDateTime timestamp;
    private final String username;

    public String getHumanReadableTimestamp(){
        return timestamp.format(FORMATTER_TIMESTAMP);
    }
}
