package Messenger.model;

import lombok.Data;

import java.util.Random;

@Data
public class User {
   private final String name;
   private final String colorCode;

    public User(String name) {
        this.name = name;
        Random r = new Random(this.name.hashCode());
        int randomColor = r.nextInt(0xffffff + 1);
        this.colorCode = String.format("#%06x", randomColor);
    }
}
