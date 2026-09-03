package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibenateControllers.CustomHibernate;
import coursework.model.User;
import coursework.persistence.Database;
import coursework.utility.PasswordUtil;
import coursework.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;

    EntityManagerFactory entityManagerFactory = Database.entityManagerFactory();

    CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);


    public void validateAndLoad() throws IOException {
        System.out.println("EntityManagerFactory: " + entityManagerFactory.hashCode());
        System.out.println("Login button clicked!");
        System.out.println("Username: " + usernameField.getText());
        // Tikrinama ar useris yra
        User user = customHibernate.getUserByLogin(usernameField.getText());
        // Jei user nera null -> load new window
        if (user != null && PasswordUtil.verifyPassword(passwordField.getText(), user.getPassword())) {
            System.out.println("User found: " + user.getLogin());

            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("main.fxml")); // Ne tik vaizda paduoda, bet ir kontrolerio klase
            Parent parent = fxmlLoader.load();
            // Po sios eilutes pasieksiu kontroleri
            Main main = fxmlLoader.getController();
            main.setData(entityManagerFactory, user);
            main.loadData(); // loads book exchange instantly after opening scene
            Scene scene = new Scene(parent);
            var stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Book Exchange Test");
            stage.setScene(scene);
            stage.show();
        } else {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "No such user", "Please check credentials!");
        }
    }



    public void newUserRegistration() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("main.fxml"));
            Parent parent = fxmlLoader.load();

            Main mainController = fxmlLoader.getController();
            mainController.setData(entityManagerFactory, null); // Pass EntityManagerFactory and null as no user is logged in

            mainController.prepareForRegistration();

            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setTitle("User registration");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            // Select the User tab
            mainController.allTabs.getSelectionModel().select(mainController.userTab);
            stage.showAndWait();


            // After registration, fetch the created user from the main controller
            User newUser = mainController.getCreatedUser();
            if (newUser != null) {
                FXMLLoader mainLoader = new FXMLLoader(StartGUI.class.getResource("main.fxml"));
                Parent mainParent = mainLoader.load();
                Main mainApp = mainLoader.getController();
                mainApp.setData(entityManagerFactory, newUser);

                Scene mainScene = new Scene(mainParent);
                var currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.setScene(mainScene);
                currentStage.setTitle("Book Exchange");
                currentStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to open the registration tab.");
        }
    }
}
