package seng302.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import seng302.model.Organs;
import seng302.model.ReceiverOrganDetails;
import seng302.model.User;
import seng302.service.Log;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReceiverOrganDateController {

    @FXML
    private Label organNameLabel;

    @FXML
    public TableView<ReceiverOrganDetails> organTimeTable;

    private User user;
    private Stage stage;

    /**
     * Initializes the receiverOrganDateView and passes important settings into the controller
     *
     * @param user  current user for this view
     * @param stage stage that the scene is shown on
     * @param organ having its history shown
     */
    public void init(User user, Stage stage, Organs organ) {
        this.user = user;
        this.stage = stage;
        organNameLabel.setText(organ.organName);
        showTimeTable(organ);
    }

    /**
     * Closes the stage on back button being pressed
     */
    @FXML
    void back() {
        stage.close();
    }

    /**
     * show organs for receiver.
     */
    private void showTimeTable(Organs organ) {
        ArrayList<LocalDate> organDates = (ArrayList<LocalDate>) user.getReceiverDetails().getOrganDates(organ);
        ArrayList<ReceiverOrganDetails> receiverOrganDetailsList = new ArrayList<>();

        if (organDates != null && !organDates.isEmpty()) {
            for (int i = 0; i < organDates.size(); i += 1) {
                ReceiverOrganDetails receiverOrganDetails = new ReceiverOrganDetails();
                receiverOrganDetails.setRegisterDate(organDates.get(i));
                i += 1;
                try {
                    receiverOrganDetails.setDeRegisterDate(organDates.get(i));
                } catch (IndexOutOfBoundsException e) {
                    Log.warning(e.getMessage(), e);
                }
                receiverOrganDetailsList.add(receiverOrganDetails);
            }
        }

        TableColumn<ReceiverOrganDetails, LocalDate> registrationDate = new TableColumn<>("Registration Date");
        registrationDate.setMinWidth(285);
        registrationDate.setCellValueFactory(new PropertyValueFactory<>("registerDate"));

        TableColumn<ReceiverOrganDetails, LocalDate> deRegistrationDate = new TableColumn<>("Deregistration Date");
        deRegistrationDate.setMinWidth(285);
        deRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("deRegisterDate"));

        ObservableList<ReceiverOrganDetails> items = FXCollections.observableList(
                receiverOrganDetailsList);

        organTimeTable.setItems(items);
        organTimeTable.getColumns().addAll(registrationDate, deRegistrationDate);

    }

}
