package coursework.model;

import coursework.model.enums.Frequency;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Periodical extends Publication{

    private int issueNumber;
    private LocalDate publicationDate;
    private String editor;
    @Enumerated
    private Frequency frequency;
    private String publisher;

    public Periodical(String title, String author, int issueNumber, LocalDate publicationDate, String editor, Frequency frequency, String publisher) {
        super(title, author);
        this.issueNumber = issueNumber;
        this.publicationDate = publicationDate;
        this.editor = editor;
        this.frequency = frequency;
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Periodical: " + title + " - (Issue: " + issueNumber + ")"  + ", Status - " + publicationStatus;
    }
}
