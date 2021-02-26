package Messenger.model;

import lombok.Data;

import java.security.Principal;

@Data
public class MyPrincipal implements Principal {
    private final String sessionId;

    @Override
    public String getName() {
        return sessionId;
    }
}