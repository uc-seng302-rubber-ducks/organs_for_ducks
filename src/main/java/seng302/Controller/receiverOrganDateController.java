package seng302.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import seng302.Model.Organs;
import seng302.Model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class receiverOrganDateController {

    @FXML
    private Label organNameLabel;

    @FXML
    private TableView<LocalDate> organTimeTable;

    @FXML
    private Button backButton;

    private AppController appController;
    private User user;
    private Stage stage;
    private ArrayList<LocalDate> registerDateList;
    private ArrayList<LocalDate> deRegisterDateList;

    /**
     * Initializes the receiverOrganDateView and passes important settings into the controller
     *
     * @param appController application state itself
     * @param user current user for this view
     * @param stage stage that the scene is shown on
     * @param organ having its history shown
     */
    public void init(AppController appController, User user, Stage stage, Organs organ){
        registerDateList = new ArrayList<>();
        this.appController = appController;
        this.user = user;
        this.stage = stage;
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
        ArrayList<LocalDate> organDates = user.getReceiverDetails().getOrganDates(organ);

        if(!organDates.isEmpty()) {
//            LocalDate registerDate;
//            LocalDate deRegisterDate;
            for (int i = 0; i < organDates.size(); i += 2) {
//                registerDate = organDates.get(i);
//                if (registerDate != null) {
//                }
                registerDateList.add(organDates.get(i));

            }

//            for (int i = 1; i < organDates.size(); i += 2) {
//                registerDateList.add(organDates.get(i));
//            }
        }

//        if (currentUser.isReceiver()) {
//            receiverOrgans = currentUser.getReceiverDetails().getOrgans();
//        }
        // use fully detailed type for Map.Entry<String, LocalDate>
        TableColumn<LocalDate, LocalDate> registrationDate = new TableColumn<>("Registration Date");
        //registrationDate.setMinWidth(285);
        registrationDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LocalDate, LocalDate>, ObservableValue<LocalDate>>() {

            @Override
            public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<LocalDate, LocalDate> p) {
                return new SimpleObjectProperty<>(p.getValue());
            }
        });

//        TableColumn<Map.Entry<Organs, LocalDate>, Organs> organName = new TableColumn<>("Organ");
//        //organName.setMinWidth(285);
//        organName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Organs, LocalDate>, Organs>, ObservableValue<Organs>>() {
//
//            @Override
//            public ObservableValue<Organs> call(TableColumn.CellDataFeatures<Map.Entry<Organs, LocalDate>, Organs> p) {
//                return new SimpleObjectProperty(p.getValue().getKey());
//            }
//        });
//        receiverOrgansTableView.setPlaceholder(new Label("Not registered as Receiver"));

        organTimeTable.getColumns().setAll(registrationDate);
//
//        if (receiverOrgans.size() != 0) {
//
            ObservableList<LocalDate> items = FXCollections.observableArrayList(registerDateList);
            organTimeTable.setItems(items);
//
//        } else {
//            receiverOrgansTableView.setPlaceholder(new Label("Not registered as Receiver"));
//        }
    }

}
