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
import org.joda.time.DateTime;
import seng302.Model.Donor;
import seng302.Model.MedicationDurations;

import java.util.ArrayList;
import java.util.Comparator;

public class MedicationsTimeController {

    @FXML
    private Label medicineNameLabel;

    @FXML
    private TableView<?> medicationTimeTable;

    @FXML
    private Button backButton;

    private AppController appController;
    private Donor donor;
    private Stage stage;
    private ObservableList medicationDurations;

    /**
     * Initilizes the view and passes important settings into the controller
     *
     * @param appController apllication state itself
     * @param donor current donor for this view
     * @param stage stage that the scene is shown on
     * @param medicine medication this is having its history shown
     */
    public void init(AppController appController, Donor donor, Stage stage, String medicine){
        this.appController = appController;
        this.donor = donor;
        this.stage = stage;
        medicineNameLabel.setText(medicine);
        ArrayList<DateTime> currentTimeStamps = donor.getCurrentMedicationTimes().get(medicine);
        ArrayList<DateTime> previousTimeStamps = donor.getPreviousMedicationTimes().get(medicine);
        medicationDurations = FXCollections.observableArrayList(new ArrayList<>());
        setUpTable(currentTimeStamps,previousTimeStamps);

    }

    /**
     * Sets up the tableview for the medicaitons
     *
     * @param current list of currently taken medication timestamps
     * @param previous list of previously taken medication timetamps
     */
    private void setUpTable(ArrayList<DateTime> current, ArrayList<DateTime> previous){
        if(current != null) {
            current.sort(Comparator.naturalOrder());
        }
        if(previous != null) {
            previous.sort(Comparator.naturalOrder());
        } else if (current.size() > 1){
            System.out.println("Somehow the medicine wasn't stopped before starting again");// TODO: fix this properly
        }
        if(previous.size() > current.size()){
            System.out.println("Somehow we have stopped taking this medicine more times than we took it"); //TODO: fix this properly
        }
        if (previous.size() == current.size()) {
            for (int i = 0; i < previous.size(); i++) {
                medicationDurations.add(new MedicationDurations(current.get(i), previous.get(i)));
            }
        } else {
            int i;
            for ( i = 0 ; i < previous.size(); i++) {
                medicationDurations.add(new MedicationDurations(current.get(i), previous.get(i)));
            }
            medicationDurations.add(new MedicationDurations(current.get(i)));

        }

        TableColumn startDateColumn = new TableColumn("Start Date");
        TableColumn endDateColumn = new TableColumn("End Date");
        TableColumn duration = new TableColumn("Duration (Days)");
        startDateColumn.setCellValueFactory( new PropertyValueFactory<MedicationDurations, String>("start"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<MedicationDurations, String>("stop"));
        duration.setCellValueFactory(new PropertyValueFactory<MedicationDurations, String>("duration"));
        medicationTimeTable.setItems(medicationDurations);
        medicationTimeTable.getColumns().addAll(startDateColumn,endDateColumn,duration);




    }


    /**
     * Closes the stage on back button being pressed
     * @param event passed in automaticly by the gui
     */
    @FXML
    void back(ActionEvent event) {
        stage.close();

    }

}
