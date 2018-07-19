package odms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import odms.commons.model.CacheManager;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.window.LoginController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * The main class of the application
 */
public class App extends Application {

    private static long bootTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //<editor-fold desc="logging setup">
        Log.setup(false);
        //</editor-fold>


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            Log.severe("failed to load login window FXML", e);
        }
        LoginController loginController = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(600);
        AppController controller = AppController.getInstance();
        primaryStage.setOnCloseRequest(event -> {
            if (primaryStage.getTitle().contains("*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "All unsaved changes will be lost, are you sure you want to quit?",
                        ButtonType.YES, ButtonType.NO);

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    CacheManager.getInstance().saveAll();
                    Platform.exit();
                    System.exit(0);
                } else {
                    event.consume();
                }
            } else {
                CacheManager.getInstance().saveAll();
            }

        });
        loginController.init(controller, primaryStage);
        primaryStage.show();
    }
}
