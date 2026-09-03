package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String body;
    private LocalDateTime timestamp;
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Comment> replies;
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    private Chat chat;
    @ManyToOne
    @JoinColumn(name = "commentOwner_id")
    private Client commentOwner;


    public Comment(String title, String body, LocalDateTime timestamp, List<Comment> replies, Comment parentComment, Client client) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.replies = replies;
        this.parentComment = parentComment;
        this.client = client;
    }

    public Comment(String title, String body, Client client, Client commentOwner) {
        this.title = title;
        this.body = body;
        this.client = client;
        this.commentOwner = commentOwner;
        this.timestamp = LocalDateTime.now();
    }

    public Comment(String title, String body, Comment parentComment, Client commentOwner) {
        this.title = title;
        this.body = body;
        this.parentComment = parentComment;
        this.commentOwner = commentOwner;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
         return commentOwner.getName() + " " + commentOwner.getSurname() + " " + timestamp + " --- " + title + ": " + body;
    }
}
