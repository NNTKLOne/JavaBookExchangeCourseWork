package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LentBooksController implements Initializable {

    @FXML
    public ListView<ReservationHistory> userReservationHistoryListView;
    @FXML
    public ListView<Publication> reservationsListView;
    @FXML
    public ListView<ReservationHistory> reservationHistoryListView;
    @FXML
    public Label reservationHistoryLabel;
    @FXML
    public Label reservationsLabel;
    @FXML
    public Label userReservationHistoryLabel;
    @FXML
    private Label userReservationHistoryCounterLabel;
    @FXML
    private Label reservationsCounterLabel;
    @FXML
    private Label reservationHistoryCounterLabel;
    @FXML
    private ComboBox<String> filterTypeComboBox;
    @FXML
    private ComboBox<String> actionComboBox;
    @FXML
    private DatePicker dateFromPicker;
    @FXML
    private DatePicker dateToPicker;

    private User currentUser;
    private GenericHibernate hibernate;

    public void setData(User currentUser, GenericHibernate hibernate) {
        this.currentUser = currentUser;
        this.hibernate = hibernate;

        if (currentUser instanceof Admin) {
            reservationHistoryLabel.setText("Reservation history of all publications");

            reservationsListView.setVisible(false);
            reservationsListView.setManaged(false);
            reservationsLabel.setVisible(false);
            reservationsLabel.setManaged(false);
            userReservationHistoryListView.setVisible(false);
            userReservationHistoryListView.setManaged(false);
            userReservationHistoryLabel.setVisible(false);
            userReservationHistoryLabel.setManaged(false);
            userReservationHistoryCounterLabel.setVisible(false);
            userReservationHistoryCounterLabel.setManaged(false);
            reservationsCounterLabel.setVisible(false);
            reservationsCounterLabel.setManaged(false);


            filterTypeComboBox.getItems().add("Reservation History of All Publications");
            filterTypeComboBox.setValue("Reservation History of All Publications");
            filterTypeComboBox.setDisable(true);

            actionComboBox.getItems().addAll("RESERVED", "RETURNED");
            loadAllReservationHistoriesForAdmin();
            updateCounter(reservationHistoryCounterLabel, reservationHistoryListView.getItems().size());
        } else {
            reservationHistoryLabel.setText("History of who reserved/returned your publications");

            reservationsListView.setVisible(true);
            reservationsListView.setManaged(true);
            reservationsLabel.setVisible(true);
            reservationsLabel.setManaged(true);
            userReservationHistoryListView.setVisible(true);
            userReservationHistoryListView.setManaged(true);
            userReservationHistoryLabel.setVisible(true);
            userReservationHistoryLabel.setManaged(true);


            filterTypeComboBox.getItems().addAll("My Reservation History", "History of who reserved/returned your publications", "Lent Publications");
            actionComboBox.getItems().addAll("RESERVED", "RETURNED");

            filterTypeComboBox.setOnAction(event -> {
                String selectedFilterType = filterTypeComboBox.getValue();
                if ("Lent Publications".equals(selectedFilterType)) {
                    actionComboBox.setDisable(true);
                    actionComboBox.getSelectionModel().clearSelection();
                } else {
                    actionComboBox.setDisable(false);
                }
            });

            loadReservations();
            loadReservationHistory();
            loadUserReservationHistory();
            updateCounter(reservationHistoryCounterLabel, reservationHistoryListView.getItems().size());
            updateCounter(reservationsCounterLabel, reservationsListView.getItems().size());
            updateCounter(userReservationHistoryCounterLabel, userReservationHistoryListView.getItems().size());

        }

        handleUserReservationHistoryClick();
        handleReservationHistoryClick();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void applyFilter() {
        String filterType = filterTypeComboBox.getValue();
        String action = actionComboBox.getValue();
        LocalDate dateFrom = dateFromPicker.getValue();
        LocalDate dateTo = dateToPicker.getValue();

        if (filterType == null) {
            showAlert("Please select a filter type.");
            return;
        }

        if (currentUser instanceof Admin && filterType.equals("Reservation History of All Publications")) {
            filterAllReservationHistories(action, dateFrom, dateTo);
        } else if (filterType.equals("My Reservation History")) {
            filterUserReservationHistory(action, dateFrom, dateTo);
        } else if (filterType.equals("History of who reserved/returned your publications")) {
            filterReservationHistory(action, dateFrom, dateTo);
        } else if (filterType.equals("Lent Publications")) {
            filterLentPublications(dateFrom, dateTo);
        }
    }

    private void filterAllReservationHistories(String action, LocalDate dateFrom, LocalDate dateTo) {
        List<ReservationHistory> filteredHistory = hibernate.getAllRecords(ReservationHistory.class).stream()
                .filter(history -> action == null || history.getAction().equals(action))
                .filter(history -> dateFrom == null || !history.getReservationDate().toLocalDate().isBefore(dateFrom))
                .filter(history -> dateTo == null || !history.getReservationDate().toLocalDate().isAfter(dateTo))
                .toList();

        reservationHistoryListView.getItems().setAll(filteredHistory);
        updateCounter(reservationHistoryCounterLabel, filteredHistory.size());
    }

    private void filterUserReservationHistory(String action, LocalDate dateFrom, LocalDate dateTo) {
        List<ReservationHistory> filteredHistory = hibernate.getAllRecords(ReservationHistory.class).stream()
                .filter(history -> history.getClient().getId() == currentUser.getId())
                .filter(history -> action == null || history.getAction().equals(action))
                .filter(history -> dateFrom == null || !history.getReservationDate().toLocalDate().isBefore(dateFrom))
                .filter(history -> dateTo == null || !history.getReservationDate().toLocalDate().isAfter(dateTo))
                .toList();

        userReservationHistoryListView.getItems().setAll(filteredHistory);
        updateCounter(userReservationHistoryCounterLabel, filteredHistory.size());
    }

    private void filterReservationHistory(String action, LocalDate dateFrom, LocalDate dateTo) {
        List<ReservationHistory> filteredHistory = hibernate.getAllRecords(ReservationHistory.class).stream()
                .filter(history -> history.getPublication().getOwner().getId() == currentUser.getId())
                .filter(history -> action == null || history.getAction().equals(action))
                .filter(history -> dateFrom == null || !history.getReservationDate().toLocalDate().isBefore(dateFrom))
                .filter(history -> dateTo == null || !history.getReservationDate().toLocalDate().isAfter(dateTo))
                .toList();

        reservationHistoryListView.getItems().setAll(filteredHistory);
        updateCounter(reservationHistoryCounterLabel, filteredHistory.size());
    }

    private void filterLentPublications(LocalDate dateFrom, LocalDate dateTo) {
        List<Publication> filteredPublications = hibernate.getAllRecords(Publication.class).stream()
                .filter(pub -> pub.getOwner() != null && pub.getOwner().getId() == currentUser.getId())
                .filter(pub -> pub.getPublicationStatus() == PublicationStatus.RESERVED)
                .filter(pub -> pub.getReservationTimestamp() != null)
                .filter(pub -> dateFrom == null || !pub.getReservationTimestamp().toLocalDate().isBefore(dateFrom))
                .filter(pub -> dateTo == null || !pub.getReservationTimestamp().toLocalDate().isAfter(dateTo))
                .toList();

        reservationsListView.getItems().setAll(filteredPublications);
        updateCounter(reservationsCounterLabel, filteredPublications.size());
    }

    private void updateCounter(Label counterLabel, int count) {
        counterLabel.setText("Total items: " + count);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadReservations() {
        reservationsListView.getItems().clear();

        if (currentUser instanceof Client client) {
            List<Publication> lentPublications = hibernate.getAllRecords(Publication.class).stream()
                    .filter(pub -> pub.getOwner() != null && pub.getOwner().getId() == client.getId()) // Match owner by ID
                    .filter(pub -> pub.getPublicationStatus() == PublicationStatus.RESERVED) // Only RESERVED publications
                    .toList();

            if (lentPublications.isEmpty()) {
                reservationsListView.setPlaceholder(new Label("No publications lent"));
            } else {
                reservationsListView.setPlaceholder(null);
                reservationsListView.getItems().addAll(lentPublications);

                reservationsListView.setCellFactory(lv -> new ListCell<Publication>() {
                    @Override
                    protected void updateItem(Publication publication, boolean empty) {
                        super.updateItem(publication, empty);
                        if (empty || publication == null) {
                            setText(null);
                        } else {
                            setText(formatLentPublicationDetails(publication));
                        }
                    }
                });
            }
        }
    }

    private String formatLentPublicationDetails(Publication publication) {
        return "Title: " + publication.getTitle() + "\n" +
                "Type: " + publication.getClass().getSimpleName() + "\n" +
                "Borrowed By: " + (publication.getClient() != null ? publication.getClient().getName() : "N/A") + "\n" +
                "Reservation Date: " + publication.getReservationTimestamp() + "\n" +
                "Status: " + publication.getPublicationStatus();
    }

    private void loadReservationHistory() {
        reservationHistoryListView.getItems().clear();

        if (currentUser instanceof Client client) {
            List<ReservationHistory> historyRecords = hibernate.getAllRecords(ReservationHistory.class).stream()
                    .filter(history -> history.getPublication().getOwner() != null) // Ensure owner is not null
                    .filter(history -> history.getPublication().getOwner().getId() == client.getId()) // Match owner by ID
                    .toList();

            if (historyRecords.isEmpty()) {
                reservationHistoryListView.setPlaceholder(new Label("You haven't lent any reservations"));
            } else {
                reservationHistoryListView.setPlaceholder(null);
                reservationHistoryListView.getItems().addAll(historyRecords);

                reservationHistoryListView.setCellFactory(lv -> new ListCell<ReservationHistory>() {
                    @Override
                    protected void updateItem(ReservationHistory history, boolean empty) {
                        super.updateItem(history, empty);
                        if (empty || history == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(formatReservationHistory(history));
                            if (history.isUnread()) {
                                setStyle("-fx-font-weight: bold; -fx-font-style: italic;");
                            } else {
                                setStyle("-fx-font-weight: normal;");
                            }
                        }
                    }
                });
            }
        }
    }

    private String formatReservationHistory(ReservationHistory history) {
        return "Title: " + history.getPublication().getTitle() + "\n" +
                "Type: " + history.getPublication().getClass().getSimpleName() + "\n" +
                "Borrowed by : " + history.getClient().getName() + "\n" +
                "Date: " + history.getReservationDate() + "\n" +
                "Action: " + history.getAction();
    }

    private void loadUserReservationHistory() {
        userReservationHistoryListView.getItems().clear();

        if (currentUser instanceof Client client) {
            List<ReservationHistory> userHistoryRecords = hibernate.getAllRecords(ReservationHistory.class).stream()
                    .filter(history -> history.getClient().getId() == client.getId()) // Match client by ID
                    .toList();

            if (userHistoryRecords.isEmpty()) {
                userReservationHistoryListView.setPlaceholder(new Label("You haven't reserved/return any reservation"));
            } else {
                userReservationHistoryListView.setPlaceholder(null);
                userReservationHistoryListView.getItems().addAll(userHistoryRecords);

                userReservationHistoryListView.setCellFactory(lv -> new ListCell<ReservationHistory>() {
                    @Override
                    protected void updateItem(ReservationHistory history, boolean empty) {
                        super.updateItem(history, empty);
                        if (empty || history == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(formatUserReservationHistory(history));
                            if (history.isUnread()) {
                                setStyle("-fx-font-weight: bold; -fx-font-style: italic;");
                            } else {
                                setStyle("-fx-font-weight: normal;");
                            }
                        }
                    }
                });
            }
        }
    }

    private String formatUserReservationHistory(ReservationHistory history) {
        return "Title: " + history.getPublication().getTitle() + "\n" +
                "Type: " + history.getPublication().getClass().getSimpleName() + "\n" +
                "Owner: " + history.getPublication().getOwner().getName() + "\n" +
                "Date: " + history.getReservationDate() + "\n" +
                "Action: " + history.getAction();
    }


    private void loadAllReservationHistoriesForAdmin() {
        reservationHistoryListView.getItems().clear();

        List<ReservationHistory> allHistoryRecords = hibernate.getAllRecords(ReservationHistory.class);

        if (allHistoryRecords.isEmpty()) {
            reservationHistoryListView.setPlaceholder(new Label("No reservation histories available"));
        } else {
            reservationHistoryListView.setPlaceholder(null);
            reservationHistoryListView.getItems().addAll(allHistoryRecords);

            reservationHistoryListView.setCellFactory(lv -> new ListCell<ReservationHistory>() {
                @Override
                protected void updateItem(ReservationHistory history, boolean empty) {
                    super.updateItem(history, empty);
                    if (empty || history == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(formatReservationHistory(history));
                        if (history.isUnread()) {
                            setStyle("-fx-font-weight: bold; -fx-font-style: italic;");
                        } else {
                            setStyle("-fx-font-weight: normal;");
                        }
                    }
                }
            });
        }
    }

    @FXML
    private void handleUserReservationHistoryClick() {
        ReservationHistory selectedHistory = userReservationHistoryListView.getSelectionModel().getSelectedItem();
        if (selectedHistory != null && selectedHistory.isUnread()) {
            selectedHistory.setUnread(false);

            hibernate.update(selectedHistory);

            userReservationHistoryListView.refresh();
        }
    }

    @FXML
    private void handleReservationHistoryClick() {
        ReservationHistory selectedHistory = reservationHistoryListView.getSelectionModel().getSelectedItem();
        if (selectedHistory != null && selectedHistory.isUnread()) {

            selectedHistory.setUnread(false);

            hibernate.update(selectedHistory);

            reservationHistoryListView.refresh();
        }
    }


}

