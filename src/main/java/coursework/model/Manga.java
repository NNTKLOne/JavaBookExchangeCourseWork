package coursework.model;

import coursework.model.enums.Demographic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manga extends Publication{

    private String illustrator;
    private String originalLanguage;
    private int volumeNumber;
    private int chapterCount;
    @Enumerated
    private Demographic demographic;
    private boolean isColor;


    public Manga(String title, String author, boolean isColor, Demographic demographic, int chapterCount, int volumeNumber, String originalLanguage, String illustrator) {
        super(title, author);
        this.isColor = isColor;
        this.demographic = demographic;
        this.chapterCount = chapterCount;
        this.volumeNumber = volumeNumber;
        this.originalLanguage = originalLanguage;
        this.illustrator = illustrator;
    }

    @Override
    public String toString() {
        return "Manga: " + title + " - (Volume: " + volumeNumber + ")" + ", Status - " + publicationStatus;
    }
}
