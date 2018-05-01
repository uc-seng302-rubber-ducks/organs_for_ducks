package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Model.ReceiverOrganDetails;

import java.time.LocalDate;
import java.util.ArrayList;

public class receiverOrganDateController {

    @FXML
    private Label organNameLabel;

    @FXML
    private TableView<ReceiverOrganDetails> organTimeTable;

    @FXML
    private Button backButton;

    private AppController appController;
    private User user;
    private Stage stage;
    private ArrayList<ReceiverOrganDetails> receiverOrganDetailsList;

    /**
     * Initializes the receiverOrganDateView and passes important settings into the controller
     *
     * @param appController application state itself
     * @param user current user for this view
     * @param stage stage that the scene is shown on
     * @param organ having its history shown
     */
    public void init(AppController appController, User user, Stage stage, Organs organ){
        this.appController = appController;
        this.user = user;
        this.stage = stage;
        System.out.println(organ);
        organNameLabel.setText(organ.organName);
        showTimeTable(organ);
    }

    /**
     * Closes the stage on back button being pressed
     * @param event passed in automatically by the gui
     */
    @FXML
    void back(ActionEvent event) {
        stage.close();
    }

    /**
     * show organs for receiver.
     */
    private void showTimeTable(Organs organ){
        ArrayList<LocalDate> organDates = (ArrayList<LocalDate>) user.getReceiverDetails().getOrganDates(organ);
        receiverOrganDetailsList = new ArrayList<>();

        if(!organDates.isEmpty()) {
            for (int i = 0; i < organDates.size(); i += 1) {
                ReceiverOrganDetails receiverOrganDetails = new ReceiverOrganDetails();
                receiverOrganDetails.setRegisterDate(organDates.get(i));
                //receiverOrganDetailsList.add(receiverOrganDetails);
                i +=1;
                try {
                    receiverOrganDetails.setDeRegisterDate(organDates.get(i));
                } catch (IndexOutOfBoundsException e){

                }
                    receiverOrganDetailsList.add(receiverOrganDetails);

            }

        }

        System.out.println("=============" + receiverOrganDetailsList.size());

        for (ReceiverOrganDetails o :
                receiverOrganDetailsList) {
            System.out.println("*********" + o.getRegisterDate());
        }


        TableColumn<ReceiverOrganDetails, LocalDate> registrationDate = new TableColumn<>("Registration Date");
        registrationDate.setMinWidth(285);
        registrationDate.setCellValueFactory(new PropertyValueFactory<>("registerDate"));

        TableColumn<ReceiverOrganDetails, LocalDate> deRegistrationDate = new TableColumn<>("Deregistration Date");
        deRegistrationDate.setMinWidth(285);
        deRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("deRegisterDate"));

        ObservableList<ReceiverOrganDetails> items = FXCollections.observableList(receiverOrganDetailsList);
        for(ReceiverOrganDetails i: items){
            System.out.println(i);
        }
        organTimeTable.setItems(items);

        organTimeTable.getColumns().addAll(registrationDate, deRegistrationDate);

    }

}
