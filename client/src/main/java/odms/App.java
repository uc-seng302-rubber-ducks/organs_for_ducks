package odms;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.commons.model.CacheManager;
import odms.commons.utils.DataHandler;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.window.LoginController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
            try {
                dataHandler.saveUsers(controller.getUsers());
                dataHandler.saveClinicians(controller.getClinicians());
                dataHandler.saveAdmins(controller.getAdmins());
                Log.info("Successfully saved all user types on exit");
                CacheManager.getInstance().saveAll();
            } catch (IOException ex) {
                Log.warning("failed to save users on exit", ex);
            }
            Platform.exit();
            System.exit(0);
        });
        loginController.init(controller, primaryStage);
        primaryStage.show();
    }
}
