package Messenger.frontend.dto;

import lombok.Data;

@Data
public class PresenceEventDto {

    public enum PresenceEventType {
        USER_LOGGED_IN,
        USER_LOGGED_OUT;
    }

    private final String username;
    private final String colorCode;
    private final PresenceEventType type;
}