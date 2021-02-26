package Messenger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Message {

    private static DateTimeFormatter FORMATTER_TIMESTAMP = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String text;
    @JsonIgnore
    private final LocalDateTime timestamp;
    private final User sender;
    private final User recipient;
    private final MessageType type;

    public String getHumanReadableTimestamp(){
        return timestamp.format(FORMATTER_TIMESTAMP);
    }
}
