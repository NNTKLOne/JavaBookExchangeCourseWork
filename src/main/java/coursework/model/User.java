package coursework.model;

import coursework.utility.PasswordUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(unique = true)
    protected String login;
    protected String password;
    protected String name;
    protected String surname;

    public User(String login, String password, String name, String surname) {
        this.login = login;
        this.password = PasswordUtil.hashPassword(password);
        this.name = name;
        this.surname = surname;
    }

}
