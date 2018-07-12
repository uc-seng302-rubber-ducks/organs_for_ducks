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
import odms.controller.AppController;
import odms.controller.gui.window.LoginController;
import odms.commons.model.CacheManager;
import odms.commons.model._enum.Directory;
import odms.commons.utils.DataHandler;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.logging.*;

/**
 * The main class of the application
 */
public class App extends Application {

    private static long bootTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    private DataHandler dataHandler = new JsonHandler();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //<editor-fold desc="logging setup">
        Logger logger = Logger.getLogger("ODMS");
        Handler handler;
        try {
            //creates file/path if it doesn't already exist
            Files.createDirectories(Paths.get(Directory.LOGS.directory()));
            handler = new FileHandler(Directory.LOGS
                    .directory() + bootTime + ".log", true);

            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            logger.addHandler(handler);

            //disables logging to console
            logger.setUseParentHandlers(false);

        } catch (IOException ex) {
            logger.log(Level.WARNING, "failed to set up logging to file", ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "failed to set up logging to file", ex);
        }
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

//                try {
//                    dataHandler.saveUsers(controller.getUsers());
//                    dataHandler.saveClinicians(controller.getClinicians());
//                    dataHandler.saveAdmins(controller.getAdmins());
//                    Log.info("Successfully saved all user types on exit");
//                    CacheManager.getInstance().saveAll();
//                } catch (IOException ex) {
//                    Log.warning("failed to save users on exit", ex);
//                }

            if (primaryStage.getTitle().contains("*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "All unsaved changes will be lost, are you sure you want to quit?",
                        ButtonType.YES, ButtonType.NO);

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    Platform.exit();
                    System.exit(0);
                } else {
                    event.consume();
                }
            }

        });
        loginController.init(controller, primaryStage);
        primaryStage.show();
    }
}
