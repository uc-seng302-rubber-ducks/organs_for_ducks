package seng302.View.Window;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Donor extends Application {

  public static void main(String[] args) { launch(args);}

  @Override
  public void start(Stage primaryStage) throws Exception {
    //shows as an error in intelliJ but works
    Parent root = FXMLLoader.load(getClass().getResource("/FXML/donorView.fxml"));
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }
}
