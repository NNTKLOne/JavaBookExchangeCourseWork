package coursework.model;

import coursework.model.enums.PublicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Publication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String title;
    protected String author;

    //Nauji is video
    @ManyToOne
    @JoinColumn(name = "owner_id")
    protected Client owner;
    @ManyToOne
    protected Client client;

    @Enumerated
    protected PublicationStatus publicationStatus;
    // reservation history new //
    @Column(name = "reservation_timestamp")
    private LocalDateTime reservationTimestamp;
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ReservationHistory> reservationHistory = new ArrayList<>();
    // reservation history new //

    public Publication(String title, String author) {
        this.title = title;
        this.author = author;
    }
    @Override
    public String toString() {
        return title + " " + author;
    }
}
