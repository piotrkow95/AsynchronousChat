package Messenger.frontend.dto;

import Messenger.model.User;
import lombok.Data;

@Data
public class PresenceEventDto {

    public enum PresenceEventType {
        USER_LOGGED_IN,
        USER_LOGGED_OUT;
    }

    private final User user;
    private final PresenceEventType type;
}