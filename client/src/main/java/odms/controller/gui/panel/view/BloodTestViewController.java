package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.User;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.gui.panel.logic.BloodTestsLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BloodTestViewController {

    @FXML
    private Button requestNewBloodTest;
    @FXML
    private Button updateBloodTest;
    @FXML
    private Button deleteBloodTest;

    @FXML
    private DatePicker bloodTestDatePicker;
    @FXML
    private TextField redBloodCount;
    @FXML
    private TextField whiteBloodCount;
    @FXML
    private TextField heamoglobin;
    @FXML
    private TextField platelets;
    @FXML
    private TextField glucose;
    @FXML
    private TextField meanCellVolume;
    @FXML
    private TextField haematocrit;
    @FXML
    private TextField meanCellHaematocrit;

    @FXML
    private Label bloodTestDateLabel;
    @FXML
    private Label bloodTestRCCountLabel;
    @FXML
    private Label bloodTestWCCountLabel;
    @FXML
    private Label bloodTestHeamoglobinLabel;
    @FXML
    private Label bloodTestPlateletsLabel;
    @FXML
    private Label bloodTestGlucoseLabel;
    @FXML
    private Label bloodTestMCVolumeLabel;
    @FXML
    private Label bloodTestHaematocritLabel;
    @FXML
    private Label bloodTestMCHaematocritLabel;

    @FXML
    private TableView<BloodTest> bloodTestTableView;
    @FXML
    private TableColumn<BloodTest, LocalDate> testDateColumn;
    @FXML
    private TableColumn<BloodTest, List<BloodTestProperties>> lowPropertyValuesColumn;
    @FXML
    private TableColumn<BloodTest, List<BloodTestProperties>> highPropertyValuesColumn;

    private ObservableList<BloodTest> bloodTests = FXCollections.observableList(new ArrayList<>());
    private BloodTestsLogicController logicController;
    private boolean fromClinician;

    /**
     * Initializes the blood test tab on the given users profile
     *
     * @param user The current user
     */
    public void init(User user, boolean fromClinician) {
        this.fromClinician = fromClinician;

        bloodTests.addListener((ListChangeListener<? super BloodTest>) observable -> {
            populateTable();
        });

        if (fromClinician) {
            showFields();
        }

        logicController = new BloodTestsLogicController(bloodTests, user);
        initBloodTestTableView();
    }

    /**
     * Makes the extra fields visible to the clinicians so blood tests can be created, updated and deleted.
     */
    private void showFields() {
        requestNewBloodTest.setVisible(true);
        updateBloodTest.setVisible(true);
        deleteBloodTest.setVisible(true);
        bloodTestDatePicker.setVisible(true);
        redBloodCount.setVisible(true);
        whiteBloodCount.setVisible(true);
        heamoglobin.setVisible(true);
        platelets.setVisible(true);
        glucose.setVisible(true);
        haematocrit.setVisible(true);
        meanCellVolume.setVisible(true);
        meanCellHaematocrit.setVisible(true);
    }

    /**
     * Initializes the table view of blood tests for the specified user
     */
    private void initBloodTestTableView() {
        testDateColumn.setCellValueFactory(new PropertyValueFactory<>("testDate"));
        lowPropertyValuesColumn.setCellValueFactory(new PropertyValueFactory<>("lowValues"));
        highPropertyValuesColumn.setCellValueFactory(new PropertyValueFactory<>("highValues"));
        logicController.updateTableView(0);
        populateTable();
        setClickOnBehaviour();
    }

    /**
     * Populates the table view with blood tests retrieved from the database
     */
    private void populateTable() {
        bloodTestTableView.setItems(bloodTests);
    }

    /**
     * Binds the table view row selection to show all details for the selected blood test
     */
    private void setClickOnBehaviour() {
        bloodTestTableView.getSelectionModel().selectedItemProperty().addListener(a -> {
            BloodTest selectedBloodTest = bloodTestTableView.getSelectionModel().getSelectedItem();

            if (selectedBloodTest != null) {
                displayBloodTestDetails(selectedBloodTest);
            } else {
                clearDetails();
            }
        });
    }

    /**
     * Displays the given blood test in more detail
     * The details are displayed as labels for users and text fields for clinicians/admins
     *
     * @param selectedBloodTest The selected blood test to be displayed in more detail
     */
    private void displayBloodTestDetails(BloodTest selectedBloodTest) {
        if (fromClinician) {
            bloodTestDatePicker.setValue(selectedBloodTest.getTestDate());
            redBloodCount.setText(Double.toString(selectedBloodTest.getRedBloodCellCount()));
            whiteBloodCount.setText(Double.toString(selectedBloodTest.getWhiteBloodCellCount()));
            heamoglobin.setText(Double.toString(selectedBloodTest.getHaemoglobinLevel()));
            platelets.setText(Double.toString(selectedBloodTest.getPlatelets()));
            glucose.setText(Double.toString(selectedBloodTest.getGlucoseLevels()));
            haematocrit.setText(Double.toString(selectedBloodTest.getHaematocrit()));
            meanCellVolume.setText(Double.toString(selectedBloodTest.getMeanCellVolume()));
            meanCellHaematocrit.setText(Double.toString(selectedBloodTest.getMeanCellHaematocrit()));

        } else {
            bloodTestDateLabel.setText(selectedBloodTest.getTestDate().toString());
            bloodTestRCCountLabel.setText(Double.toString(selectedBloodTest.getRedBloodCellCount()));
            bloodTestWCCountLabel.setText(Double.toString(selectedBloodTest.getWhiteBloodCellCount()));
            bloodTestHeamoglobinLabel.setText(Double.toString(selectedBloodTest.getHaemoglobinLevel()));
            bloodTestPlateletsLabel.setText(Double.toString(selectedBloodTest.getPlatelets()));
            bloodTestGlucoseLabel.setText(Double.toString(selectedBloodTest.getGlucoseLevels()));
            bloodTestMCVolumeLabel.setText(Double.toString(selectedBloodTest.getMeanCellVolume()));
            bloodTestHaematocritLabel.setText(Double.toString(selectedBloodTest.getHaematocrit()));
            bloodTestMCHaematocritLabel.setText(Double.toString(selectedBloodTest.getMeanCellHaematocrit()));
        }
    }

    /**
     * Clears the labels and text fields when no blood test is selected
     */
    private void clearDetails() {
        if (fromClinician) {
            bloodTestDatePicker.setValue(null);
            redBloodCount.setText("");
            whiteBloodCount.setText("");
            heamoglobin.setText("");
            platelets.setText("");
            glucose.setText("");
            haematocrit.setText("");
            meanCellVolume.setText("");
            meanCellHaematocrit.setText("");
        } else {
            bloodTestDateLabel.setText("");
            bloodTestRCCountLabel.setText("");
            bloodTestWCCountLabel.setText("");
            bloodTestHeamoglobinLabel.setText("");
            bloodTestPlateletsLabel.setText("");
            bloodTestGlucoseLabel.setText("");
            bloodTestMCVolumeLabel.setText("");
            bloodTestHaematocritLabel.setText("");
            bloodTestMCHaematocritLabel.setText("");
        }
    }

    @FXML
    private void updateBloodTest() {
        logicController.updateBloodTest();

    }

    @FXML
    private void deleteBloodTest() {
        if (bloodTestTableView.getSelectionModel().getSelectedItem() != null) {
            logicController.deleteBloodTest(bloodTestTableView.getSelectionModel().getSelectedItem());
        } else {
            AlertWindowFactory.generateInfoWindow("You must select an blood test to delete");
        }
    }

    @FXML
    private void goToNextPage() {
        logicController.gotoNextPage();

    }

    @FXML
    private void goToPreviousPage() {
        logicController.goToPreviousPage();

    }

    @FXML
    private void addNewBloodTest() {
        logicController.addNewBloodTest();
    }


}
