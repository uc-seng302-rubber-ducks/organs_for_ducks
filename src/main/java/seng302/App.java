package seng302;


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
public class App extends Application
{
    public static void main(String[] args) { launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        //This looks confusing for now but ill explain it next time we have a stand up
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoginController loginController = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(650);
        AppController controller = AppController.getInstance();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    JsonHandler.saveUsers(controller.getUsers());
                } catch (IOException ex) {
                    System.out.println("failed to save users");
                }
                Platform.exit();
                System.exit(0);
            }
        });
        loginController.init(controller, primaryStage);
        primaryStage.show();
    }
}
