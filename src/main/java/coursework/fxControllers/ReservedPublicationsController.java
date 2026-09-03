package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import coursework.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ReservedPublicationsController implements Initializable {

    @FXML
    private ListView<String> reservedPublicationsList;

    private User currentUser;
    private GenericHibernate hibernate;

    public void setData(User currentUser, GenericHibernate hibernate) {
        this.currentUser = currentUser;
        this.hibernate = hibernate;
        loadReservedPublications();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    private void loadReservedPublications() {
        reservedPublicationsList.getItems().clear();

        if (currentUser instanceof Admin) {
            List<Publication> reservedPublications = hibernate.getAllRecords(Publication.class).stream()
                    .filter(pub -> pub.getPublicationStatus() == PublicationStatus.RESERVED)
                    .toList();

            for (Publication publication : reservedPublications) {
                reservedPublicationsList.getItems().add(formatPublicationDetails(publication));
            }

        } else if (currentUser instanceof Client) {

            Client client = hibernate.getEntityById(Client.class, currentUser.getId());

            // Clients see only their borrowed publications with RESERVED status
            List<Publication> borrowedReservedPublications = client.getBorrowedPublications().stream()
                    .filter(pub -> pub.getPublicationStatus() == PublicationStatus.RESERVED)
                    .toList();

            for (Publication publication : borrowedReservedPublications) {
                reservedPublicationsList.getItems().add(formatPublicationDetails(publication));
            }
        }
    }





    private String formatPublicationDetails(Publication publication) {
        if (publication == null) return "No details available.";

        StringBuilder details = new StringBuilder();

        ;
        if (publication instanceof Book book) {
            details.append("ID: ").append(publication.getId()).append("\n")
                    .append("Type: ").append("Book\n")
                    .append("Title: ").append(book.getTitle()).append("\n")
                    .append("Author: ").append(book.getAuthor()).append("\n")
                    .append("Publisher: ").append(book.getPublisher()).append("\n")
                    .append("ISBN: ").append(book.getIsbn()).append("\n")
                    .append("Genre: ").append(book.getGenre()).append("\n")
                    .append("Page Count: ").append(book.getPageCount()).append("\n")
                    .append("Language: ").append(book.getLanguage()).append("\n")
                    .append("Publication Year: ").append(book.getPublicationYear()).append("\n")
                    .append("Format: ").append(book.getFormat()).append("\n")
                    .append("Summary: ").append(book.getSummary()).append("\n");
        } else if (publication instanceof Manga manga) {
            details.append("ID: ").append(publication.getId()).append("\n")
                    .append("Type: ").append("Manga\n")
                    .append("Title: ").append(manga.getTitle()).append("\n")
                    .append("Author: ").append(manga.getAuthor()).append("\n")
                    .append("Illustrator: ").append(manga.getIllustrator()).append("\n")
                    .append("Color: ").append(manga.isColor() ? "Yes" : "No").append("\n")
                    .append("Demographic: ").append(manga.getDemographic()).append("\n")
                    .append("Chapters: ").append(manga.getChapterCount()).append("\n")
                    .append("Volume: ").append(manga.getVolumeNumber()).append("\n")
                    .append("Original Language: ").append(manga.getOriginalLanguage()).append("\n");
        } else if (publication instanceof Periodical periodical) {
            details.append("ID: ").append(publication.getId()).append("\n")
                    .append("Type: ").append("Periodical\n")
                    .append("Title: ").append(periodical.getTitle()).append("\n")
                    .append("Author: ").append(periodical.getAuthor()).append("\n")
                    .append("Publisher: ").append(periodical.getPublisher()).append("\n")
                    .append("Editor: ").append(periodical.getEditor()).append("\n")
                    .append("Issue Number: ").append(periodical.getIssueNumber()).append("\n")
                    .append("Publication Date: ").append(periodical.getPublicationDate()).append("\n")
                    .append("Frequency: ").append(periodical.getFrequency()).append("\n");
        } else {
            details.append("General Publication\n")
                    .append("Title: ").append(publication.getTitle()).append("\n")
                    .append("Author: ").append(publication.getAuthor()).append("\n");
        }

        // Common fields
        details.append("Status: ").append(publication.getPublicationStatus() != null ? publication.getPublicationStatus() : "N/A").append("\n");
        if (publication.getClient() != null) {
            details.append("Reserved By: ").append(publication.getClient().getName()).append("\n");
        }

        return details.toString();
    }

    public void returnPublication() {
        int selectedIndex = reservedPublicationsList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "No Selection", "Please select a publication to return.");
            return;
        }

        String selectedItem = reservedPublicationsList.getSelectionModel().getSelectedItem();

        try {
            int selectedPublicationId = extractIdFromDetails(selectedItem);

            Publication selectedPublication = hibernate.getEntityById(Publication.class, selectedPublicationId);

            if (selectedPublication != null && selectedPublication.getPublicationStatus() == PublicationStatus.RESERVED) {
                Client client = selectedPublication.getClient();

                ReservationHistory returnHistory = new ReservationHistory(
                        selectedPublication,
                        client,
                        LocalDateTime.now()
                );
                returnHistory.setAction("RETURNED");
                selectedPublication.getReservationHistory().add(returnHistory);

                selectedPublication.setPublicationStatus(PublicationStatus.AVAILABLE);
                selectedPublication.setClient(null);
                selectedPublication.setReservationTimestamp(null);

                hibernate.update(selectedPublication);

                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Publication has been returned.");
                loadReservedPublications();
            } else {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Invalid Operation", "This publication cannot be returned.");
            }
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to parse the selected item. Please ensure it is formatted correctly.");
        }
    }

    private int extractIdFromDetails(String details) {
        try {
            // Look for "ID: X" in the details string
            int idStartIndex = details.indexOf("ID: ") + 4; // Start after "ID: "
            int idEndIndex = details.indexOf("\n", idStartIndex); // End at the next newline
            String idString = details.substring(idStartIndex, idEndIndex).trim(); // Extract the ID as a string
            return Integer.parseInt(idString); // Convert to an integer
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse ID from details: " + details, e);
        }
    }






}
