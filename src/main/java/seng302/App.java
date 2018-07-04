package seng302;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng302.controller.AppController;
import seng302.controller.gui.window.LoginController;
import seng302.model.CacheManager;
import seng302.model._enum.Directory;
import seng302.utils.DataHandler;
import seng302.utils.JsonHandler;
import seng302.utils.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
