package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seng302.Model.Organs;
import seng302.Model.User;

import java.util.ArrayList;
import java.util.Arrays;

public class ReceiverOrganController {

    @FXML
    private ComboBox<Organs> organsComboBox;

    @FXML
    private ListView<Organs> currentlyReceivingListView;

    @FXML
    private ListView<Organs> notReceivingListView;

    AppController controller;
    Stage stage;
    private User currentUser;

    private ArrayList<Organs> organs;


    /**
     * Initializes the ReceiverOrganController
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(User user, AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        currentUser = user;
        //showCurrentDate();
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
        organs = new ArrayList<>(Arrays.asList(Organs.values()));
        organsComboBox.setItems(FXCollections.observableList(organs));
    }

    /**
     * register an organ
     * for receiver
     */
    public void registerOrgan(){
        //TODO: link add organ functionality to receiver profile

        Organs toRegister = organsComboBox.getSelectionModel().getSelectedItem();
        if(!currentlyReceivingListView.getItems().contains(toRegister)) {
            currentlyReceivingListView.getItems().add(toRegister);
        }
    }

    /**
     * re-register an organ
     * for receiver
     */
    public void reRegisterOrgan(){
        //TODO: link add organ functionality to receiver profile

        Organs toReRegister = notReceivingListView.getSelectionModel().getSelectedItem();
        if(toReRegister != null) {
            currentlyReceivingListView.getItems().add(toReRegister);
            notReceivingListView.getItems().remove(toReRegister);
        }
    }

    /**
     * de-register an organ
     * for receiver
     */
    public void deRegisterOrgan(){
        //TODO: link remove organ functionality to receiver profile

        Organs toDeRegister = currentlyReceivingListView.getSelectionModel().getSelectedItem();
        if(toDeRegister != null) {
            notReceivingListView.getItems().add(toDeRegister);
            currentlyReceivingListView.getItems().remove(toDeRegister);
        }
    }

}
