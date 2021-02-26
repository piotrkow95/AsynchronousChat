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
   private final String principalName;

    public User(String name, String principalName) {
        this.name = name;
        this.principalName = principalName;
        Random r = new Random(this.name.hashCode());
        int randomColor = r.nextInt(0xffffff + 1);
        this.colorCode = String.format("#%06x", randomColor);
    }
}
