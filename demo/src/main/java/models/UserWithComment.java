package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UserWithComment {
    @NonNull
    private User user;
    @NonNull
    private Comment comment;


    @Override
    public String toString() {
        return "UserWithComment{" +
                  user  + comment +
                '}';
    }
}
