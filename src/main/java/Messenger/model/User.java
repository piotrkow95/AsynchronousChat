package Messenger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private final String avatarUrl;
    @JsonIgnore
    private final String principalName;

    public User(String name, String principalName, String avatarUrl) {
        this.name = name;
        this.principalName = principalName;
        this.avatarUrl = avatarUrl;
        Random r = new Random(this.name.hashCode());
        int randomColor = r.nextInt(0xffffff + 1);
        this.colorCode = String.format("#%06x", randomColor);
    }
}
