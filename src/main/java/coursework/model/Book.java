package coursework.model;

import coursework.model.enums.Format;
import coursework.model.enums.Genre;
import coursework.model.enums.Language;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book extends Publication{


    private String publisher;
    private String isbn;
    private Genre genre;
    private int pageCount;
    private Language language;
    private int publicationYear;
    private Format format; // Hardcover, Paperback, Digital, etc.
    private String summary;

    public Book(String title, String author, String publisher, String isbn, Genre genre, int pageCount, Language language, int publicationYear, Format format, String summary) {
        super(title, author);
        this.publisher = publisher;
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
        this.language = language;
        this.publicationYear = publicationYear;
        this.format = format;
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Book: " + title + " (Year: " + publicationYear + ")" + ", Status - " + publicationStatus;
    }
}
