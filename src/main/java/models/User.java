package models;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NonNull
public class User {
    @NonNull//This are used when you need one parameter and that parameter can't be null
    private String name;
    private String lastName;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
