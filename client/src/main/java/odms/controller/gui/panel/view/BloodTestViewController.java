package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.User;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.AttributeValidation;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.BloodTestsLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private BloodTest bloodTest;

    /**
     * Initializes the blood test tab on the given users profile
     *
     * @param user The current user
     */
    public void init(AppController controller, User user, boolean fromClinician) {
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
                bloodTest = selectedBloodTest;
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
            redBloodCount.setText(getString(selectedBloodTest.getRedBloodCellCount()));
            whiteBloodCount.setText(getString(selectedBloodTest.getWhiteBloodCellCount()));
            heamoglobin.setText(getString(selectedBloodTest.getHaemoglobinLevel()));
            platelets.setText(getString(selectedBloodTest.getPlatelets()));
            glucose.setText(getString(selectedBloodTest.getGlucoseLevels()));
            haematocrit.setText(getString(selectedBloodTest.getHaematocrit()));
            meanCellVolume.setText(getString(selectedBloodTest.getMeanCellVolume()));
            meanCellHaematocrit.setText(getString(selectedBloodTest.getMeanCellHaematocrit()));

        } else {
            bloodTestDateLabel.setText(selectedBloodTest.getTestDate().toString());
            bloodTestRCCountLabel.setText(getString(selectedBloodTest.getRedBloodCellCount()));
            bloodTestWCCountLabel.setText(getString(selectedBloodTest.getWhiteBloodCellCount()));
            bloodTestHeamoglobinLabel.setText(getString(selectedBloodTest.getHaemoglobinLevel()));
            bloodTestPlateletsLabel.setText(getString(selectedBloodTest.getPlatelets()));
            bloodTestGlucoseLabel.setText(getString(selectedBloodTest.getGlucoseLevels()));
            bloodTestMCVolumeLabel.setText(getString(selectedBloodTest.getMeanCellVolume()));
            bloodTestHaematocritLabel.setText(getString(selectedBloodTest.getHaematocrit()));
            bloodTestMCHaematocritLabel.setText(getString(selectedBloodTest.getMeanCellHaematocrit()));
        }
    }

    /**
     * Used to find the appropriate string to set the text field to.
     * If the given value is 0.0, the text field is set to the empty string.
     *
     * @param value The value of a blood test property
     * @return The given value as a string if not 0.0, otherwise an empty string
     */
    private String getString(double value) {
        if (value == 0.0) {
            if (fromClinician) {
                return "";
            } else {
                return "This property was not tested";
            }
        }

        return Double.toString(value);
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

    private void invalidateNode(Node node) {
        node.getStyleClass().add("invalid");
    }


    private boolean validateField() {
        boolean valid = true;
        if (AttributeValidation.validateDouble(redBloodCount.getText()) == -1) {
            bloodTestRCCountLabel.setVisible(true);
            invalidateNode(redBloodCount);
            valid = false;
        }
        if (AttributeValidation.validateDouble(whiteBloodCount.getText()) == -1) {
            bloodTestWCCountLabel.setVisible(true);
            invalidateNode(whiteBloodCount);
            valid = false;

        }
        if (AttributeValidation.validateDouble(heamoglobin.getText()) == -1) {
            bloodTestHeamoglobinLabel.setVisible(true);
            invalidateNode(heamoglobin);
            valid = false;
        }
        if (AttributeValidation.validateDouble(platelets.getText()) == -1) {
            bloodTestPlateletsLabel.setVisible(true);
            invalidateNode(platelets);
            valid = false;
        }
        if (AttributeValidation.validateDouble(glucose.getText()) == -1) {
            bloodTestGlucoseLabel.setVisible(true);
            invalidateNode(glucose);
            valid = false;
        }
        if (AttributeValidation.validateDouble(meanCellVolume.getText()) == -1) {
            bloodTestMCVolumeLabel.setVisible(true);
            invalidateNode(meanCellVolume);
            valid = false;
        }
        if (AttributeValidation.validateDouble(haematocrit.getText()) == -1) {
            bloodTestHaematocritLabel.setVisible(true);
            invalidateNode(haematocrit);
            valid = false;
        }
        if (AttributeValidation.validateDouble(meanCellHaematocrit.getText()) == -1) {
            bloodTestMCHaematocritLabel.setVisible(true);
            invalidateNode(meanCellHaematocrit);
            valid = false;
        }
        if (!AttributeValidation.validateDateBeforeTomorrow(bloodTestDatePicker.getValue())) {
            bloodTestDateLabel.setVisible(true);
            invalidateNode(bloodTestDatePicker);
            valid = false;
        }
        return valid;
    }

    @FXML
    private void updateBloodTest() {
            if (validateField()) {
                // hmmm seems bad sadness
                bloodTest.setGlucoseLevels(AttributeValidation.validateDouble(glucose.getText()));
                bloodTest.setHaematocrit(AttributeValidation.validateDouble(haematocrit.getText()));
                bloodTest.setMeanCellHaematocrit(AttributeValidation.validateDouble(meanCellHaematocrit.getText()));
                bloodTest.setPlatelets(AttributeValidation.validateDouble(platelets.getText()));
                bloodTest.setWhiteBloodCellCount(AttributeValidation.validateDouble(whiteBloodCount.getText()));
                bloodTest.setRedBloodCellCount(AttributeValidation.validateDouble(redBloodCount.getText()));
                bloodTest.setMeanCellVolume(AttributeValidation.validateDouble(meanCellVolume.getText()));
                bloodTest.setHaemoglobinLevel(AttributeValidation.validateDouble(heamoglobin.getText()));
                bloodTest.setTestDate(bloodTestDatePicker.getValue());
                logicController.updateBloodTest(bloodTest);
                AlertWindowFactory.generateInfoWindow("Blood Test on: "+ bloodTest.getTestDate() +" updated");
            }
    }

    @FXML
    private void deleteBloodTest() {
        if (bloodTestTableView.getSelectionModel().getSelectedItem() != null) {
            Optional<ButtonType> result = AlertWindowFactory.generateConfirmation("Are you sure you want to delete this blood test?");

            if (!result.isPresent()) {
            return;
        }

            if (result.get() == ButtonType.OK) {
                logicController.deleteBloodTest(bloodTestTableView.getSelectionModel().getSelectedItem());
            }
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
