package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Client extends User implements Comparable<Client> {

    private String address;
    //birthDate prideta demonstracijai kaip dirbt su LocalDate
    private LocalDate birthDate;
    private String clientBio;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> commentList;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Publication> ownedPublications;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Publication> borrowedPublications;
    @OneToMany(mappedBy = "commentOwner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> myComments;

    public Client(String login, String password, String name, String surname, String address) {
        super(login, password, name, surname);
        this.address = address;
    }

    public Client(String login, String password, String name, String surname, String address, LocalDate birthDate) {
        super(login, password, name, surname);
        this.address = address;
        this.birthDate = birthDate;

    }

    public Client(String login, String password, String name, String surname, String address, LocalDate birthDate, String clientBio) {
        super(login, password, name, surname);
        this.address = address;
        this.birthDate = birthDate;
        this.clientBio = clientBio;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }

    @Override
    public int compareTo(Client o) {

        return 0;
    }
}
