package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    //    @OneToOne
//    @JoinColumn(name = "publication_id")
//    private Publication publication;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Comment> comments;



    public Chat(String title) {
        this.title = title;
    }

    public Chat(String title, List<Comment> comments) {
        this.title = title;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Chat Title: " + title;
    }
}

