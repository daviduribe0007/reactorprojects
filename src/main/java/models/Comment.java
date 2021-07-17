package models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Comment {
    private List<String> comments;


    public Comment() {
        this.comments = new ArrayList<>();
    }

    public void setComments(String comments) {//This method add one to one all comments
        this.comments.add( comments);
    }

    @Override
    public String toString() {
        return "comments=" + comments ;
    }
}
