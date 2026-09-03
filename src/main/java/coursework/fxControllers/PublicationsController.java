package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.*;
import coursework.persistence.Database;
import coursework.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import jakarta.persistence.EntityManagerFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PublicationsController implements Initializable {

    @FXML
    public ListView<Publication> publicationListView;

    // Common fields
    @FXML
    public TextField titleField;
    @FXML
    public TextField authorField;

    // Book-specific fields
    @FXML
    public TextField publisherField;
    @FXML
    public TextField isbnField;
    @FXML
    public ComboBox<Genre> genreField;
    @FXML
    public TextField pageCountField;
    @FXML
    public ComboBox<Language> languageField;
    @FXML
    public TextField publicationYearField;
    @FXML
    public ComboBox<Format> formatField;
    @FXML
    public TextField summaryField;

    // Manga-specific fields
    @FXML
    public CheckBox isColorCheckBox;
    @FXML
    public ComboBox<Demographic> demographicField;
    @FXML
    public TextField chapterCountField;
    @FXML
    public TextField volumeNumberField;
    @FXML
    public ComboBox<Language> originalLanguageField;
    @FXML
    public TextField illustratorField;

    // Periodical-specific fields
    @FXML
    public TextField issueNumberField;
    @FXML
    public DatePicker publicationDateField;  // Periodical-specific field
    @FXML
    public TextField editorField;
    @FXML
    public ComboBox<Frequency> frequencyField;

    @FXML
    public RadioButton bookRadio;
    @FXML
    public RadioButton mangaRadio;
    @FXML
    public RadioButton periodicalRadio;
    @FXML
    public Button addPublicationButton;
    public Button viewReservationsButton;

    private EntityManagerFactory entityManagerFactory = Database.entityManagerFactory();
    private GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    private User currentUser;

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.hibernate = new GenericHibernate(entityManagerFactory);
        this.currentUser = user;
        System.out.println("User received in PublicationsController: " + (user != null ? user.getLogin() : "NULL"));
        if (currentUser instanceof Admin) {
            addPublicationButton.setVisible(false);
            viewReservationsButton.setVisible(true);
        } else {
            addPublicationButton.setVisible(true);
            viewReservationsButton.setVisible(true);
        }
        fillPublicationList();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize ComboBoxes with enum values
        genreField.getItems().setAll(Genre.values());
        languageField.getItems().setAll(Language.values());
        formatField.getItems().setAll(Format.values());
        demographicField.getItems().setAll(Demographic.values());
        originalLanguageField.getItems().setAll(Language.values());
        frequencyField.getItems().setAll(Frequency.values());

        // Creating a publication starts with no implicit type. The user must
        // choose a type before any publication fields become editable.
        bookRadio.getToggleGroup().selectToggle(null);
        applySelectedTypeState();
        fillPublicationList();
    }

    public void viewReservations() throws IOException {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("lentBooksWindow.fxml"));
            Parent parent = fxmlLoader.load();

            LentBooksController controller = fxmlLoader.getController();
            controller.setData(currentUser, hibernate);
            Stage stage = new Stage();
            stage.setTitle("Lent books");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        }catch (IOException e) {
            e.printStackTrace();
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Unable to load the reservations view.");
        }
    }


    private boolean validatePublicationInput() {
        String errorMessage = "";

        if (!bookRadio.isSelected() && !mangaRadio.isSelected() && !periodicalRadio.isSelected()) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Publication Type Required",
                    "Please select Book, Manga, or Periodical first.");
            return false;
        }

        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage += "Title is required!\n";
        }
        if (authorField.getText() == null || authorField.getText().isEmpty()) {
            errorMessage += "Author is required!\n";
        }

        // Validation for Book-specific fields
        if (bookRadio.isSelected()) {
            if (publisherField.getText() == null || publisherField.getText().isEmpty()) {
                errorMessage += "Publisher is required for books!\n";
            }
            if (genreField.getValue() == null) {
                errorMessage += "Genre must be selected for books!\n";
            }
            if (languageField.getValue() == null) {
                errorMessage += "Language must be selected for books!\n";
            }
            if (formatField.getValue() == null) {
                errorMessage += "Format must be selected for books!\n";
            }

            try {
                Integer.parseInt(pageCountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Page count must be a number!\n";
            }
            try {
                Integer.parseInt(publicationYearField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Publication year must be a number!\n";
            }
        }

        // Validation for Manga-specific fields
        if (mangaRadio.isSelected()) {
            if (demographicField.getValue() == null) {
                errorMessage += "Demographic must be selected for manga!\n";
            }
            if (originalLanguageField.getValue() == null) {
                errorMessage += "Original Language must be selected for manga!\n";
            }
            try {
                Integer.parseInt(chapterCountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Chapter count must be a number!\n";
            }
            try {
                Integer.parseInt(volumeNumberField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Volume number must be a number!\n";
            }
        }

        // Validation for Periodical-specific fields
        if (periodicalRadio.isSelected()) {
            if (frequencyField.getValue() == null) {
                errorMessage += "Frequency must be selected for periodicals!\n";
            }
            if (publicationDateField.getValue() == null) {
                errorMessage += "Publication Date is required for periodicals!\n";
            }
            try {
                Integer.parseInt(issueNumberField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Issue number must be a number!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;  // All fields are valid
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Validation Error", errorMessage);
            return false;  // There are validation errors
        }
    }

    public void fillPublicationList() {
        publicationListView.getItems().clear();
        if(currentUser instanceof Admin) {
            List<Publication> publications = hibernate.getAllRecords(Publication.class);
            publicationListView.getItems().addAll(publications);
        }
        else if (currentUser instanceof Client client) {
            // Client can only view their own publications
            List<Publication> clientPublications = hibernate.getAllRecords(Publication.class).stream()
                    .filter(pub -> pub.getOwner() != null && pub.getOwner().getId() == client.getId())
                    .toList();
            publicationListView.getItems().addAll(clientPublications);
        }
    }

    public boolean isPublicationDuplicate(Publication publication) {
        List<Publication> publications = hibernate.getAllRecords(Publication.class);
        for (Publication existingPublication : publications) {
            if (existingPublication.getTitle().equals(publication.getTitle())) {
                return true;
            }
        }
        return false;
    }
    public void createNewPublication() {

        if (!validatePublicationInput()) {
            return;
        }

        Publication publication = null;

        if (bookRadio.isSelected()) {
            publication = new Book(
                    titleField.getText(),
                    authorField.getText(),
                    publisherField.getText(),
                    isbnField.getText(),
                    genreField.getValue(),
                    Integer.parseInt(pageCountField.getText()),
                    languageField.getValue(),
                    Integer.parseInt(publicationYearField.getText()),
                    formatField.getValue(),
                    summaryField.getText()
            );
        } else if (mangaRadio.isSelected()) {
            publication = new Manga(
                    titleField.getText(),
                    authorField.getText(),
                    isColorCheckBox.isSelected(),
                    demographicField.getValue(),
                    Integer.parseInt(chapterCountField.getText()),
                    Integer.parseInt(volumeNumberField.getText()),
                    originalLanguageField.getValue().toString(),
                    illustratorField.getText()
            );
        } else if (periodicalRadio.isSelected()) {
            publication = new Periodical(
                    titleField.getText(),
                    authorField.getText(),
                    Integer.parseInt(issueNumberField.getText()),
                    publicationDateField.getValue(),
                    editorField.getText(),
                    frequencyField.getValue(),
                    publisherField.getText()
            );
        }
        if (isPublicationDuplicate(publication)) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Duplicate Entry", "A publication with this name already exists!");
            return;
        }
        if(publication.getPublicationStatus() == null) {
            publication.setPublicationStatus(PublicationStatus.AVAILABLE);
        }
        if (publication != null) {
            if(currentUser instanceof Client client)
            publication.setOwner(client); // Set the logged-in user as the owner
            else {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Invalid Operation", "Only Clients can create publications.");
                return;
            }
            hibernate.create(publication);
            fillPublicationList(); // Refresh the list
        }
    }

    public void updatePublication() {
        Publication selectedPublication = publicationListView.getSelectionModel().getSelectedItem();

        if (!validatePublicationInput()) {
            return;
        }

        if (selectedPublication != null) {
            selectedPublication.setTitle(titleField.getText());
            selectedPublication.setAuthor(authorField.getText());

            if (selectedPublication instanceof Book) {
                Book book = (Book) selectedPublication;
                book.setPublisher(publisherField.getText());
                book.setIsbn(isbnField.getText());
                book.setGenre(genreField.getValue());
                book.setPageCount(Integer.parseInt(pageCountField.getText()));
                book.setLanguage(languageField.getValue());
                book.setPublicationYear(Integer.parseInt(publicationYearField.getText()));
                book.setFormat(formatField.getValue());
                book.setSummary(summaryField.getText());
            } else if (selectedPublication instanceof Manga) {
                Manga manga = (Manga) selectedPublication;
                manga.setColor(isColorCheckBox.isSelected());
                manga.setDemographic(demographicField.getValue());
                manga.setChapterCount(Integer.parseInt(chapterCountField.getText()));
                manga.setVolumeNumber(Integer.parseInt(volumeNumberField.getText()));
                manga.setOriginalLanguage(originalLanguageField.getValue().toString());
                manga.setIllustrator(illustratorField.getText());
            } else if (selectedPublication instanceof Periodical) {
                Periodical periodical = (Periodical) selectedPublication;
                periodical.setIssueNumber(Integer.parseInt(issueNumberField.getText()));
                periodical.setPublicationDate(publicationDateField.getValue());
                periodical.setEditor(editorField.getText());
                periodical.setFrequency(frequencyField.getValue());
                periodical.setPublisher(publisherField.getText());
            }

            hibernate.update(selectedPublication);
            fillPublicationList();
        }
    }


public void deletePublication() {
        Publication selectedPublication = publicationListView.getSelectionModel().getSelectedItem();

        if (selectedPublication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "No Selection", "Please select a publication to delete.");
            return;
        }

        try {
            // Retrieve the full entity from the database using the ID
            Publication publicationFromDb = hibernate.getEntityById(Publication.class, selectedPublication.getId());

            if (publicationFromDb == null) {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Not Found", "Selected publication does not exist in the database.");
                return;
            }

            // Prevent deletion if the publication status is RESERVED
            if (publicationFromDb.getPublicationStatus() == PublicationStatus.RESERVED) {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Operation Denied", "Cannot delete a publication that is currently RESERVED.");
                return;
            }

            // Perform the deletion
            hibernate.delete(Publication.class, publicationFromDb.getId());
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Publication deleted successfully.");

            // Refresh the publication list
            fillPublicationList();
        } catch (Exception e) {
            e.printStackTrace();
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the publication. Please try again.");
        }
    }







    @FXML
    public void loadPublicationData() {
        // Get the selected publication from the ListView
        Publication selectedPublication = publicationListView.getSelectionModel().getSelectedItem();

        if (selectedPublication == null) {
            return;
        }

        // Retrieve the full publication details from the database
        Publication publicationInfoFromDb = hibernate.getEntityById(Publication.class, selectedPublication.getId());
        clearFields();
        if (publicationInfoFromDb != null) {
            if (publicationInfoFromDb instanceof Book) {
                bookRadio.setSelected(true);
            } else if (publicationInfoFromDb instanceof Manga) {
                mangaRadio.setSelected(true);
            } else if (publicationInfoFromDb instanceof Periodical) {
                periodicalRadio.setSelected(true);
            }
            applySelectedTypeState();

            // Set common fields for all publications
            titleField.setText(publicationInfoFromDb.getTitle());
            authorField.setText(publicationInfoFromDb.getAuthor());

            if (publicationInfoFromDb instanceof Book) {
                // Handle Book-specific fields
                Book book = (Book) publicationInfoFromDb;
                publisherField.setText(book.getPublisher());
                isbnField.setText(book.getIsbn());
                genreField.setValue(book.getGenre());
                pageCountField.setText(String.valueOf(book.getPageCount()));
                languageField.setValue(book.getLanguage());
                publicationYearField.setText(String.valueOf(book.getPublicationYear()));
                formatField.setValue(book.getFormat());
                summaryField.setText(book.getSummary());
            } else if (publicationInfoFromDb instanceof Manga) {
                // Handle Manga-specific fields
                Manga manga = (Manga) publicationInfoFromDb;
                isColorCheckBox.setSelected(manga.isColor());
                demographicField.setValue(manga.getDemographic());
                chapterCountField.setText(String.valueOf(manga.getChapterCount()));
                volumeNumberField.setText(String.valueOf(manga.getVolumeNumber()));
                originalLanguageField.setValue(Language.valueOf(manga.getOriginalLanguage()));
                illustratorField.setText(manga.getIllustrator());
            } else if (publicationInfoFromDb instanceof Periodical) {
                // Handle Periodical-specific fields
                Periodical periodical = (Periodical) publicationInfoFromDb;
                issueNumberField.setText(String.valueOf(periodical.getIssueNumber()));
                publicationDateField.setValue(periodical.getPublicationDate());  // Set DatePicker with LocalDate
                editorField.setText(periodical.getEditor());
                frequencyField.setValue(periodical.getFrequency());
                publisherField.setText(periodical.getPublisher());
            }
        }
    }

    public void disableFields() {
        clearFields();
        applySelectedTypeState();
    }

    private void applySelectedTypeState() {
        boolean typeSelected = bookRadio.isSelected() || mangaRadio.isSelected() || periodicalRadio.isSelected();

        titleField.setDisable(!typeSelected);
        authorField.setDisable(!typeSelected);
        addPublicationButton.setDisable(!typeSelected);

        // Start disabled, then enable only the controls belonging to the
        // selected publication type.
        publisherField.setDisable(true);
        isbnField.setDisable(true);
        genreField.setDisable(true);
        pageCountField.setDisable(true);
        languageField.setDisable(true);
        publicationYearField.setDisable(true);
        formatField.setDisable(true);
        summaryField.setDisable(true);

        isColorCheckBox.setDisable(true);
        demographicField.setDisable(true);
        chapterCountField.setDisable(true);
        volumeNumberField.setDisable(true);
        originalLanguageField.setDisable(true);
        illustratorField.setDisable(true);

        issueNumberField.setDisable(true);
        publicationDateField.setDisable(true);
        editorField.setDisable(true);
        frequencyField.setDisable(true);

        if (bookRadio.isSelected()) {
            publisherField.setDisable(false);
            isbnField.setDisable(false);
            genreField.setDisable(false);
            pageCountField.setDisable(false);
            languageField.setDisable(false);
            publicationYearField.setDisable(false);
            formatField.setDisable(false);
            summaryField.setDisable(false);
        } else if (mangaRadio.isSelected()) {
            isColorCheckBox.setDisable(false);
            demographicField.setDisable(false);
            chapterCountField.setDisable(false);
            volumeNumberField.setDisable(false);
            originalLanguageField.setDisable(false);
            illustratorField.setDisable(false);
        } else if (periodicalRadio.isSelected()) {
            // Periodicals also use the shared publisher field.
            publisherField.setDisable(false);
            issueNumberField.setDisable(false);
            publicationDateField.setDisable(false);
            editorField.setDisable(false);
            frequencyField.setDisable(false);
        }
    }
    public void clearFields() {
        // Clear common fields
        titleField.clear();
        authorField.clear();

        // Clear Book-specific fields
        publisherField.clear();
        isbnField.clear();
        genreField.setValue(null);
        pageCountField.clear();
        languageField.setValue(null);
        publicationYearField.clear();
        formatField.setValue(null);
        summaryField.clear();


        // Clear Manga-specific fields
        isColorCheckBox.setSelected(false);
        demographicField.setValue(null);
        chapterCountField.clear();
        volumeNumberField.clear();
        originalLanguageField.setValue(null);
        illustratorField.clear();

        // Clear Periodical-specific fields
        issueNumberField.clear();
        publicationDateField.setValue(null);
        editorField.clear();
        frequencyField.setValue(null);

    }


}
