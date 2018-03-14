package seng302;


import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng302.Controller.AppController;
import seng302.Controller.DonorController;
import seng302.Model.Donor;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

import static javafx.application.Application.launch;

public class App extends Application
{
    public static void main(String[] args) { launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        //This looks confusing for now but ill explain it next time we have a stand up
        FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/donorView.fxml"));
        Parent root = donorLoader.load();
        primaryStage.setScene(new Scene(root));
        DonorController donorController =  donorLoader.getController();
        donorController.init(AppController.getInstance());
        primaryStage.show();
    }
}
