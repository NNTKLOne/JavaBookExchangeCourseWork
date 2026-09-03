package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;
    @Column(name = "action")
    private String action;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private LocalDateTime reservationDate;

    private boolean unread = true;

    public ReservationHistory(Publication publication, Client client, LocalDateTime reservationDate) {
        this.publication = publication;
        this.client = client;
        this.reservationDate = reservationDate;
    }


}
