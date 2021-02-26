package Messenger.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Random;

@Getter
@ToString
@EqualsAndHashCode
public class User {
   private final String name;
   private final String colorCode;
   private final String sessionId;

    public User(String name, String sessionId) {
        this.name = name;
        this.sessionId= sessionId;
        Random r = new Random(this.name.hashCode());
        int randomColor = r.nextInt(0xffffff + 1);
        this.colorCode = String.format("#%06x", randomColor);
    }
}
