package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.model.*;
import coursework.model.enums.*;
import coursework.utility.PasswordUtil;
import coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Main implements Initializable {
    @FXML
    public ListView<User> userListField;
    @FXML
    public TextField loginField;
    @FXML
    public TextField nameField;
    @FXML
    public PasswordField pswField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField addressField;
    @FXML
    public DatePicker bDate;
    @FXML
    public TextField clientBioField;
    @FXML
    public TextField phoneNumField;
    @FXML
    public RadioButton adminChk;
    @FXML
    public RadioButton clientChk;
    @FXML
    public Button updateUserButton;
    @FXML
    public Button deleteUserButton;
    //<editor-fold desc="Tableview attributes">
    @FXML
    public TableView<UserTableParameters> userTable;
    @FXML
    public TableColumn<UserTableParameters, Integer> colID;
    @FXML
    public TableColumn<UserTableParameters, String> colLogin;
    @FXML
    public TableColumn<UserTableParameters, String> colPassword;
    @FXML
    public TableColumn<UserTableParameters, String> colName;
    @FXML
    public TableColumn<UserTableParameters, String> colSurname;
    @FXML
    public TableColumn<UserTableParameters, String> colAddress;
    @FXML
    public TableColumn<UserTableParameters, String> colPhone;
    @FXML
    public TableColumn<UserTableParameters, LocalDate> colBirthDate;
    public TableColumn<UserTableParameters, String> colBio;

    public ObservableList<UserTableParameters> data = FXCollections.observableArrayList();
    @FXML
    public TableColumn dummyCol;
    //</editor-fold>
//<editor-fold desc="Main Book exchange tab fields">
    @FXML
    public ListView<Publication> availableBookList;
    @FXML
    public TextArea aboutBook;
    @FXML
    public TextArea ownerBio;
    @FXML
    public Label ownerInfo;
    @FXML
    public ListView<Comment> chatList;
    @FXML
    public TextArea messageArea;
    @FXML
    public Button chatWithOwner;

    @FXML
    public Tab publicationManagementTab;
    @FXML
    public Tab userManagementTab;
    @FXML
    public Tab userTab;
    @FXML
    public Tab clientBookManagementTab;
    @FXML
    public TabPane allTabs;
    @FXML
    public Button leaveReviewButton;
    @FXML
    public Tab bookExchangeTab;
    public Tab loginTab;
    public Button viewReservedPublicationsButton;
    public Tab chatTab;
    public Button reserveBookButton;

    @FXML
    public ComboBox<String> publicationTypeComboBox;
    @FXML
    public ComboBox<Genre> genreComboBox;
    @FXML
    public ComboBox<Language> languageComboBox;
    @FXML
    public ComboBox<Demographic> demographicComboBox;
    @FXML
    public CheckBox isColorCheckBox;
    @FXML
    public ComboBox<Frequency> frequencyComboBox;
    @FXML
    public TextField publicationYearField;
    @FXML
    public Button filterButton;
//</editor-fold>


    private EntityManagerFactory entityManagerFactory; // cj galima istrint

    private CustomHibernate hibernate;

    private User currentUser;

    // nauji updeitai 11/06 d
    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.hibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = user;
        fillUserList();

        // Priklausomai nuo prisijungusio vartotojo tipo, apribojam matomuma
        enableVisibility();
    }
    //Tabs, buttons
    private void enableVisibility() {
        if(currentUser instanceof Client){
            //allTabs.getTabs().remove(publicationManagementTab);
            allTabs.getTabs().remove(userManagementTab);
            allTabs.getTabs().remove(userTab);
            allTabs.getTabs().remove(loginTab);
            viewReservedPublicationsButton.setDisable(false);
            allTabs.getTabs().remove(chatTab);
            reserveBookButton.setDisable(false);
        }
        else {
            allTabs.getTabs().remove(clientBookManagementTab);
            leaveReviewButton.setDisable(false);
            allTabs.getTabs().remove(loginTab);
            viewReservedPublicationsButton.setDisable(false);
            allTabs.getTabs().remove(chatTab);
            reserveBookButton.setDisable(true);
        }
    }
    public void adjustFieldAccessibility() {
        String selectedType = publicationTypeComboBox.getValue();

        if (selectedType == null) {
            // Enable all fields by default
            setFieldsDisabled(false, false, false, false, false, false);
            return;
        }

        switch (selectedType) {
            case "Book":
                setFieldsDisabled(false, false, true, true, true, false);
                break;
            case "Manga":
                setFieldsDisabled(true, true, false, true, false, true);
                break;
            case "Periodical":
                setFieldsDisabled(true, true, true, false, true, true);
                break;
        }
    }

    private void setFieldsDisabled(boolean language, boolean genre, boolean demographic, boolean frequency, boolean isColor, boolean publicationYear) {
        languageComboBox.setDisable(language);
        genreComboBox.setDisable(genre);
        demographicComboBox.setDisable(demographic);
        frequencyComboBox.setDisable(frequency);
        isColorCheckBox.setDisable(isColor);
        publicationYearField.setDisable(publicationYear);
    }

    //Filter for book Exchange tab
    public void filterAvailableBooks() {
        String selectedType = publicationTypeComboBox.getValue();
        Genre selectedGenre = genreComboBox.getValue();
        Language selectedLanguage = languageComboBox.getValue();
        Demographic selectedDemographic = demographicComboBox.getValue();
        boolean isColor = isColorCheckBox.isSelected();
        Frequency selectedFrequency = frequencyComboBox.getValue();
        String publicationYear = publicationYearField.getText();

        List<Publication> allAvailablePublications = hibernate.getAvailablePublications(currentUser);
        List<Publication> filteredPublications = new ArrayList<>();


        for (Publication pub : allAvailablePublications) {
            if (selectedType != null) {
                if (selectedType.equals("Book") && !(pub instanceof Book)) continue;
                if (selectedType.equals("Manga") && !(pub instanceof Manga)) continue;
                if (selectedType.equals("Periodical") && !(pub instanceof Periodical)) continue;
            }
            if (pub instanceof Book book) {
                if (selectedGenre != null && !selectedGenre.equals(book.getGenre())) continue;
                if (selectedLanguage != null && !selectedLanguage.equals(book.getLanguage())) continue;
                if (publicationYear != null && !publicationYear.isEmpty() && book.getPublicationYear() != Integer.parseInt(publicationYear)) continue;
            }
            if (pub instanceof Manga manga) {
                if (selectedDemographic != null && !selectedDemographic.equals(manga.getDemographic())) continue;
                if (isColor && !manga.isColor()) continue;
            }
            if (pub instanceof Periodical periodical) {
                if (selectedFrequency != null && !selectedFrequency.equals(periodical.getFrequency())) continue;
            }
            filteredPublications.add(pub);
        }

        availableBookList.getItems().setAll(filteredPublications);
    }

    public void fillUserList() {
        if(hibernate == null) {
            System.out.println("Hibernate is null in fillUserList");
            return;
        }
        userListField.getItems().clear();
        List<User> userList = hibernate.getAllRecords(User.class);
        userListField.getItems().addAll(userList);
    }

    private void fillUserTable() {
        userTable.getItems().clear();
        List<User> allUsers = hibernate.getAllRecords(User.class);
        for(User u:allUsers) {
            UserTableParameters userTableParameters = new UserTableParameters();
            userTableParameters.setId(u.getId());
            userTableParameters.setLogin(u.getLogin());
            userTableParameters.setPassword(u.getPassword());
            userTableParameters.setName(u.getName());
            userTableParameters.setSurname(u.getSurname());

            if (u instanceof Client) {
                Client client = (Client) u;
                userTableParameters.setAddress(client.getAddress());
                userTableParameters.setBirthDate(client.getBirthDate());
                userTableParameters.setClientBio(client.getClientBio());
            }
            else if (u instanceof Admin) {
                Admin admin = (Admin) u;
                userTableParameters.setPhone(admin.getPhoneNum());
            }

            userTable.getItems().add(userTableParameters);
        }
    }
    //--------------------------------------------------------------------------------

    public boolean isUserDuplicate(User user) {
        List<User> users = hibernate.getAllRecords(User.class);
        for (User existingUser : users) {
            if (existingUser.getLogin().equals(user.getLogin())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateUserInput() {
        String errorMessage = "";

        if (loginField.getText() == null || loginField.getText().isEmpty()) {
            errorMessage += "Login is required!\n";
        }
        if (pswField.getText() == null || pswField.getText().isEmpty()) {
            errorMessage += "Password is required!\n";
        }
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Name is required!\n";
        }
        if (surnameField.getText() == null || surnameField.getText().isEmpty()) {
            errorMessage += "Surname is required!\n";
        }
        if (clientChk.isSelected()) {
            if (addressField.getText() == null || addressField.getText().isEmpty()) {
                errorMessage += "Address is required for clients!\n";
            }
            if (bDate.getValue() == null) {
                errorMessage += "Birthdate is required for clients!\n";
            }
            if (clientBioField.getText() == null || clientBioField.getText().isEmpty()) {
                errorMessage += "Client bio is required for clients!\n";
            }
        } else if (adminChk.isSelected()) {
            if (phoneNumField.getText() == null || phoneNumField.getText().isEmpty()) {
                errorMessage += "Phone number is required for admins!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;  // All good
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Validation Error", errorMessage);
            return false;
        }
    }

    public void createNewUser() {

        if (!validateUserInput()) {
            return;
        }

        User newUser = null;

        if (clientChk.isSelected()) {
            newUser = new Client(
                    loginField.getText(),
                    pswField.getText(),
                    nameField.getText(),
                    surnameField.getText(),
                    addressField.getText(),
                    bDate.getValue(),
                    clientBioField.getText());
        } else {
            newUser = new Admin(
                    loginField.getText(),
                    pswField.getText(),
                    nameField.getText(),
                    surnameField.getText(),
                    phoneNumField.getText());
        }

        if (isUserDuplicate(newUser)) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Duplicate Entry", "A user with this login already exists!");

        }

        hibernate.create(newUser);
        fillUserList();

        setCreatedUser(newUser);
        enableAllTabs();

        Stage stage = (Stage) userListField.getScene().getWindow();
        stage.close();

    }

    public void disableFields() {
        if (clientChk.isSelected()) {
            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
            clientBioField.setDisable(false);
        } else {
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
            clientBioField.setDisable(true);
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Filtering components
        availableBookList.setPlaceholder(new Label("No publications available from other users."));
        publicationTypeComboBox.setItems(FXCollections.observableArrayList("Book", "Manga", "Periodical"));
        genreComboBox.setItems(FXCollections.observableArrayList(Genre.values()));
        languageComboBox.setItems(FXCollections.observableArrayList(Language.values()));
        demographicComboBox.setItems(FXCollections.observableArrayList(Demographic.values()));
        frequencyComboBox.setItems(FXCollections.observableArrayList(Frequency.values()));

        userTable.setEditable(true);
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colBio.setCellValueFactory(new PropertyValueFactory<>("clientBio"));


        colLogin.setCellFactory(TextFieldTableCell.forTableColumn());
        colLogin.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setLogin(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setLogin(event.getNewValue());
            hibernate.update(user);
        });

        colPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        colPassword.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPassword(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setPassword(PasswordUtil.hashPassword(event.getNewValue()));
            hibernate.update(user);
        });

        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setName(event.getNewValue());
            hibernate.update(user);
        });

        colSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSurname.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setSurname(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setSurname(event.getNewValue());
            hibernate.update(user);
        });

        colAddress.setCellFactory(TextFieldTableCell.forTableColumn());
        colAddress.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setAddress(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Client client) {
                client.setAddress(event.getNewValue());
                hibernate.update(user);
            }
        });

        colBirthDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        colBirthDate.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setBirthDate(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Client client) {
                client.setBirthDate(event.getNewValue());
                hibernate.update(user);
            }
        });

        colBio.setCellFactory(TextFieldTableCell.forTableColumn());
        colBio.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setClientBio(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Client client) {
                client.setClientBio(event.getNewValue());
                hibernate.update(user);
            }
        });

        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhone(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Admin admin) {
                admin.setPhoneNum(event.getNewValue());
                hibernate.update(admin);
            }
        });

        //knopke
        Callback<TableColumn<UserTableParameters, Void>, TableCell<UserTableParameters, Void>> callback = param -> {
            final TableCell<UserTableParameters, Void> cell = new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        UserTableParameters row = getTableView().getItems().get(getIndex());
                        hibernate.delete(User.class, row.getId());
                        fillUserTable();
                        fillUserList();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        };
        dummyCol.setCellFactory(callback);
    }

    public void loadUserData() {

        User selectedUser = userListField.getSelectionModel().getSelectedItem();

        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        loginField.setText(userInfoFromDb.getLogin());
        nameField.setText(userInfoFromDb.getName());
        surnameField.setText(userInfoFromDb.getSurname());
        pswField.setText(userInfoFromDb.getPassword());

        if (userInfoFromDb instanceof Client) {
            Client client = (Client) userInfoFromDb;
            addressField.setText(client.getAddress());
            bDate.setValue(client.getBirthDate());

            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.clear();
            phoneNumField.setDisable(true);
            clientBioField.clear();
            clientBioField.setDisable(false);

        } else {
            Admin admin = (Admin) userInfoFromDb;
            phoneNumField.setText(admin.getPhoneNum());

            addressField.setDisable(true);
            addressField.clear();
            bDate.setDisable(true);
            bDate.setValue(null);
            phoneNumField.setDisable(false);
            clientBioField.setDisable(true);
            clientBioField.clear();
        }

    }

    public void updateUser() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();

        if (!validateUserInput()) {
            return;
        }

        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        userInfoFromDb.setName(nameField.getText());
        userInfoFromDb.setSurname(surnameField.getText());
        userInfoFromDb.setLogin(loginField.getText());

        String hashedPassword = PasswordUtil.hashPassword(pswField.getText());
        userInfoFromDb.setPassword(hashedPassword);

        if (userInfoFromDb instanceof Client) {
            Client client = (Client) userInfoFromDb;
            client.setAddress(addressField.getText());
            client.setBirthDate(bDate.getValue());
            client.setClientBio(clientBioField.getText());
        } else {
            Admin admin = (Admin) userInfoFromDb;
            admin.setPhoneNum(phoneNumField.getText());
        }

        hibernate.update(userInfoFromDb);
        fillUserList();
    }

    public void deleteUser() {

        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        hibernate.delete(User.class, selectedUser.getId());
        fillUserList();
    }

    public void loadProductForm() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("productWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Book Exchange Test");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }
    public void loadReviewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("userReview.fxml"));
        Parent parent = fxmlLoader.load();
        UserReview userReview = fxmlLoader.getController();
        userReview.setData(entityManagerFactory, currentUser, availableBookList.getSelectionModel().getSelectedItem().getOwner());
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Comments");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
    public void chatWithOwner() {
    }

    public void reserveBook() {
        Publication selectedPublication = availableBookList.getSelectionModel().getSelectedItem();

        if (selectedPublication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "No Selection", "Please select a publication to reserve!");
            return;
        }

        // Fetch the latest managed instance of the selected publication
        Publication managedPublication = hibernate.getEntityById(Publication.class, selectedPublication.getId());

        if (managedPublication.getPublicationStatus() == PublicationStatus.RESERVED) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Already Reserved", "This publication is already reserved!");
            return;
        }

        if (currentUser instanceof Client client) {
            // Fetch the latest managed instance of the client
            Client managedClient = hibernate.getEntityById(Client.class, client.getId());

            managedPublication.setPublicationStatus(PublicationStatus.RESERVED);
            managedPublication.setClient(managedClient);
            managedPublication.setReservationTimestamp(LocalDateTime.now());

            ReservationHistory history = new ReservationHistory(managedPublication, managedClient, LocalDateTime.now());
            history.setAction("RESERVED");
            managedPublication.getReservationHistory().add(history);

            managedClient.getBorrowedPublications().add(managedPublication);

            hibernate.update(managedClient);

            availableBookList.getItems().remove(selectedPublication);

            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Publication reserved successfully!");
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Invalid User", "Only clients can reserve publications.");
        }
    }


    public void viewReservedPublications() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("reservedPublications.fxml"));
        Parent parent = fxmlLoader.load();

        ReservedPublicationsController controller = fxmlLoader.getController();

        if (currentUser instanceof Client client) {
            controller.setData(client, hibernate);
        } else if (currentUser instanceof Admin admin) {
            controller.setData(admin, hibernate);
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Access Denied", "BUG");
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Reserved Publications");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }





    //Kai spaudziama ant tab, tam tab pildome duomenis
    public void loadData() {
        if(userManagementTab.isSelected()){
            fillUserTable();

        } else if(bookExchangeTab.isSelected()){
            availableBookList.getItems().clear();
            availableBookList.getItems().addAll(hibernate.getAvailablePublications(currentUser));
        }
    }

    public void loadPublicationInfo() {
        Publication publication = availableBookList.getSelectionModel().getSelectedItem();

        // A ListView mouse handler also fires when its empty area is clicked.
        if (publication == null) {
            aboutBook.clear();
            ownerBio.clear();
            ownerInfo.setText("");
            return;
        }

        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());

        if (publicationFromDb == null) {
            loadData();
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Not Found",
                    "This publication no longer exists.");
            return;
        }

        if(publicationFromDb instanceof Book book) {
            aboutBook.setText(
                    "Title: " + book.getTitle() + "\n" +
                            "Author: " + book.getAuthor() + "\n" +
                            "Publisher: " + book.getPublisher() + "\n" +
                            "ISBN: " + book.getIsbn() + "\n" +
                            "Genre: " + book.getGenre() + "\n" +
                            "Page Count: " + book.getPageCount() + "\n" +
                            "Language: " + book.getLanguage() + "\n" +
                            "Publication Year: " + book.getPublicationYear() + "\n" +
                            "Format: " + book.getFormat() + "\n" +
                            "Summary: " + book.getSummary()
            );

        }
        if(publicationFromDb instanceof Manga manga) {
            aboutBook.setText(
                    "Title: " + manga.getTitle() + "\n" +
                            "Author: " + manga.getAuthor() + "\n" +
                            "Illustrator: " + manga.getIllustrator() + "\n" +
                            "Color: " + (manga.isColor() ? "Yes" : "No") + "\n" +
                            "Demographic: " + manga.getDemographic() + "\n" +
                            "Chapters: " + manga.getChapterCount() + "\n" +
                            "Volume: " + manga.getVolumeNumber() + "\n" +
                            "Original Language: " + manga.getOriginalLanguage()
            );

        }
        if(publicationFromDb instanceof Periodical periodical) {
            aboutBook.setText(
                    "Title: " + periodical.getTitle() + "\n" +
                            "Author: " + periodical.getAuthor() + "\n" +
                            "Publisher: " + periodical.getPublisher() + "\n" +
                            "Editor: " + periodical.getEditor() + "\n" +
                            "Issue Number: " + periodical.getIssueNumber() + "\n" +
                            "Publication Date: " + periodical.getPublicationDate() + "\n" +
                            "Frequency: " + periodical.getFrequency()

            );

        }

        Client owner = publicationFromDb.getOwner();
        if (owner == null) {
            ownerInfo.setText("No owner information");
            ownerBio.clear();
        } else {
            ownerInfo.setText(owner.getName() + " " + owner.getSurname());
            ownerBio.setText(owner.getClientBio() == null ? "" : owner.getClientBio());
        }

    }
    public void prepareForRegistration() {
        allTabs.getSelectionModel().select(userTab);

        for (Tab tab : allTabs.getTabs()) {
            if (!tab.equals(userTab)) {
                tab.setDisable(true);
            }
        }
        userListField.setDisable(true);
        userListField.getItems().clear();

        updateUserButton.setDisable(true);
        deleteUserButton.setDisable(true);

    }
    public void enableAllTabs() {
        for (Tab tab : allTabs.getTabs()) {
            tab.setDisable(false);
        }
        userListField.setDisable(false);
        updateUserButton.setDisable(false);
        deleteUserButton.setDisable(false);
        fillUserList();
    }

    public void loadPublications(Tab publicationManagementTab) throws IOException {
        System.out.println("Current user in Main: " + (currentUser != null ? currentUser.getLogin() : "NULL"));
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("publications.fxml"));
        Parent parent = fxmlLoader.load();

        PublicationsController controller = fxmlLoader.getController();
        controller.setData(entityManagerFactory, currentUser);
        System.out.println("Current user passed to PublicationsController: " + currentUser);

        publicationManagementTab.setContent(parent);
    }

    public void onPublicationsTabSelected() throws IOException {
        if (publicationManagementTab.isSelected()) {
            loadPublications(publicationManagementTab);
        }
    }

    private User createdUser;

    public void setCreatedUser(User user) {
        this.createdUser = user;
    }

    public User getCreatedUser() {
        return createdUser;
    }


}
