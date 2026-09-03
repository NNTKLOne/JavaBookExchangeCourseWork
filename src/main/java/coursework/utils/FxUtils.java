package coursework.utils;

import javafx.scene.control.Alert;

public class FxUtils {

    public static void generateAlert(Alert.AlertType alertType, String title, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
