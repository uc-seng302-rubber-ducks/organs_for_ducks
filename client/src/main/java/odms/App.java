package odms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model.CacheManager;
import odms.commons.model._enum.Environments;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.window.LoginController;
import utils.AppConfigurator;
import utils.StageIconLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * The main class of the application
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        //<editor-fold desc="config">
        ConfigPropertiesSession session = ConfigPropertiesSession.getInstance();
        AppConfigurator configurator = new AppConfigurator(session);
        configurator.setupArguments(super.getParameters(), "clientConfig.properties");
        configurator.setupLogging(Environments.TEST);


        AppController controller = AppController.getInstance();
        configurator.setupWebsocket(controller);
        //</editor-fold>

        //<editor-fold desc="fxml setupArguments">
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

        if (!ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equalsIgnoreCase("true")) {
            URL url = getClass().getResource("/logos/LoveDuck.png");
            if (url == null) {
                Log.warning("Could not load the icon for the taskbar. Check that the filepath is correct");
            } else {
                javafx.scene.image.Image image = new Image(url.openStream());
                primaryStage.getIcons().add(image);
            }
        }



        loginController.init(controller, primaryStage);
        if (!ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equalsIgnoreCase("true")) {
            StageIconLoader stageIconLoader = new StageIconLoader();
            primaryStage.getIcons().add(stageIconLoader.getIconImage());
        }
        primaryStage.show();
    }

    /**
     * runs all the various methods needed to run at shutdown
     */
    private void shutdown() {
        CacheManager.getInstance().saveAll();
        AppController.getInstance().stop();
        AppController.getInstance().getClient().connectionPool().evictAll();
        AppController.getInstance().getClient().dispatcher().executorService().shutdown();
        Platform.exit();
        System.exit(0);
    }
}
