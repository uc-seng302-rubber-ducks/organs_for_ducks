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
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model.CacheManager;
import odms.commons.model._enum.Environments;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.window.LoginController;

import java.io.IOException;
import java.util.Optional;

/**
 * The main class of the application
 */
public class App extends Application {

    public static void main(String[] args) {
        getProperties(args);
        launch(args);
    }

    /**
     * reads the command line args and and adds them to the ConfigPropertiesSession
     *
     * @param args arguments to interpret
     */
    protected static void getProperties(String[] args) {
        ConfigPropertiesSession session = ConfigPropertiesSession.getInstance();
        session.loadFromFile("clientConfig.properties");
        if (args == null || args.length == 0) {
            return;
        }
        for (String arg : args) {
            String[] split = arg.split("=");
            if (split.length == 2) {
                session.setProperty(split[0], split[1]);
            } else {
                Log.warning("bad commandline arg \"" + arg + "\" has been ignored");
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Log.setup(Environments.TEST);

        //<editor-fold desc="fxml setup">
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
        controller.getAllowedCountries();
        //</editor-fold>

        //app shutdown handler
        primaryStage.setOnCloseRequest(event -> {
            if (primaryStage.getTitle().contains("*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "All unsaved changes will be lost, are you sure you want to quit?",
                        ButtonType.YES, ButtonType.NO);

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    shutdown();
                } else {
                    event.consume();
                }
            } else {
                //actually shutting down this time
                shutdown();
            }

        });
        AppController.getInstance().getSocketHandler().start(ConfigPropertiesSession.getInstance().getProperty("server.websocket.url", "ws://localhost:4941/websocket"));
        loginController.init(controller, primaryStage);
        primaryStage.show();
    }

    /**
     * runs all the various methods needed to run at shutdown
     */
    private void shutdown() {
        CacheManager.getInstance().saveAll();
        AppController.getInstance().stop();
        Platform.exit();
        System.exit(0);
    }
}
