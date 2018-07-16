package odms.controller.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.Optional;

public class UnsavedChangesAlert {

    public static Optional<ButtonType> getAlertResult() {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "You have unsaved changes, do you want to save first?",
                ButtonType.YES, ButtonType.NO);

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);



        return alert.showAndWait();
    }
}
