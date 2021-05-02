package Messenger.model;

import lombok.Value;

import java.util.Set;

@Value
public class Conversation {
    /**
     * Set of principalNames
     */
    Set<String> participants;
}
