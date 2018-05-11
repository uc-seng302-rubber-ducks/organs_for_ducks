package seng302;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seng302.Controller.AppController;
import seng302.Controller.LoginController;
import seng302.Model.JsonHandler;

import java.io.IOException;

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

    Logger x = Logger.getLogger("ODMS");
    Handler handler;

    try {
      Files.createDirectories(Paths.get(Directory.LOGS.directory()));
      handler = new FileHandler(Directory.LOGS
          .directory() + bootTime + ".log", true);
      x.addHandler(handler);

    } catch (IOException ex) {
      x.log(Level.WARNING, "failed to set up logging to file", ex);
    } catch (SecurityException ex) {
      x.log(Level.SEVERE, "failed to set up logging to file", ex);
    }

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
    Parent root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    LoginController loginController = loader.getController();
    primaryStage.setScene(new Scene(root));
    AppController controller = AppController.getInstance();
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        try {
          JsonHandler.saveUsers(controller.getUsers());
          x.info("Successfully saved users on exit");
        } catch (IOException ex) {
          x.warning("failed to save users on exit");
        }
        Platform.exit();
        System.exit(0);
      }
    });
    loginController.init(controller, primaryStage);
    primaryStage.show();
  }
}
