package odms.controller.gui.popup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.MedicationDurations;
import odms.commons.utils.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class for the functionality of the medication view
 */
public class MedicationsTimeController {

    @FXML
    private Label medicineNameLabel;

    @FXML
    private TableView<?> medicationTimeTable;

    private Stage stage;
    private ObservableList medicationDurations;

    /**
     * Initializes the view and passes important settings into the controller
     *
     * @param user     current user for this view
     * @param stage    stage that the scene is shown on
     * @param medicine medication this is having its history shown
     */
    public void init(User user, Stage stage, String medicine) {
        this.stage = stage;
        medicineNameLabel.setText(medicine);
        List<LocalDateTime> currentTimeStamps = user.getCurrentMedicationTimes(medicine);
        List<LocalDateTime> previousTimeStamps = user.getPreviousMedicationTimes(medicine);
        medicationDurations = FXCollections.observableArrayList(new ArrayList<>());
        setUpTable(currentTimeStamps, previousTimeStamps);

    }

    /**
     * Sets up the tableview for the medicaitons
     *
     * @param current  list of currently taken medication timestamps
     * @param previous list of previously taken medication timetamps
     */
    private void setUpTable(List<LocalDateTime> current, List<LocalDateTime> previous) {
        if (current != null) {
            current.sort(Comparator.naturalOrder());
        } else {
            current = new ArrayList<>();
        }
        if (previous != null) {
            previous.sort(Comparator.naturalOrder());
        } else {
            previous = new ArrayList<>();
        }
        if (current.size() > 1) {
            Log.info("Somehow the medicine wasn't stopped before starting again");
        }
        if (previous.size() > current.size()) {
            Log.info("Somehow we have stopped taking this medicine more times than we took it");
        }
        if (previous.size() == current.size()) {
            for (int i = 0; i < previous.size(); i++) {
                medicationDurations.add(new MedicationDurations(current.get(i), previous.get(i)));
            }
        } else {
            int i;
            for (i = 0; i < previous.size(); i++) {
                medicationDurations.add(new MedicationDurations(current.get(i), previous.get(i)));
            }
            medicationDurations.add(new MedicationDurations(current.get(i)));

        }

        TableColumn startDateColumn = new TableColumn("Start Date");
        TableColumn endDateColumn = new TableColumn("End Date");
        TableColumn durationColumn = new TableColumn("Duration (Days)");
        startDateColumn.setCellValueFactory(new PropertyValueFactory<MedicationDurations, String>("start"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<MedicationDurations, String>("stop"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<MedicationDurations, String>("duration"));
        medicationTimeTable.setItems(medicationDurations);
        medicationTimeTable.getColumns().addAll(startDateColumn, endDateColumn, durationColumn);


    }


    /**
     * Closes the stage on back button being pressed
     */
    @FXML
    void back() {
        stage.close();

    }

}
