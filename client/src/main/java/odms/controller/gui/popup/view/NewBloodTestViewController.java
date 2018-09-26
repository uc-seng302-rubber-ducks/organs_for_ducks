package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.bridge.BloodTestBridge;
import odms.commons.model.User;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.utils.AttributeValidation;
import odms.controller.gui.popup.logic.NewBloodTestLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import static odms.commons.utils.BloodTestUtils.*;


public class NewBloodTestViewController {
    @FXML
    private DatePicker testDate;
    @FXML
    private TextField redBloodCount;
    @FXML
    private TextField whiteBloodCount;
    @FXML
    private TextField heamoglobin;
    @FXML
    private  TextField platelets;
    @FXML
    private  TextField glucose;
    @FXML
    private  TextField meanCellVolume;
    @FXML
    private  TextField haematocrit;
    @FXML
    private  TextField meanCellHaematocrit;
    @FXML
    private Button addBloodTest;
    @FXML
    private Button cancelBloodTest;
    @FXML
    private Label dateErrorLabel;
    @FXML
    private Label redBloodCellError;
    @FXML
    private Label whiteBloodCellError;
    @FXML
    private Label heamoglobinError;
    @FXML
    private Label plateletsError;
    @FXML
    private Label glucoseError;
    @FXML
    private Label meanCellVolumeError;
    @FXML
    private Label haematocritError;
    @FXML
    private Label meanCellHaematocritError;



    private NewBloodTestLogicController logicController;
    private boolean atLeastOneValue = false;

    public void init(User user, Stage stage, BloodTestBridge bloodTestBridge){
        this.logicController = new NewBloodTestLogicController(user,stage);
        resetErrorLabels();
        textFieldListener(redBloodCount);
        textFieldListener(whiteBloodCount);
        textFieldListener(haematocrit);
        textFieldListener(heamoglobin);
        textFieldListener(platelets);
        textFieldListener(meanCellHaematocrit);
        textFieldListener(meanCellVolume);
        textFieldListener(glucose);
        datePickerListener(testDate);
    }

    @FXML
    public void cancel() {
        logicController.cancel();
    }

    /**
     * resets the error label to be hidden
     */
    private void resetErrorLabels(){
        dateErrorLabel.setVisible(false);
        redBloodCellError.setVisible(false);
        whiteBloodCellError.setVisible(false);
        heamoglobinError.setVisible(false);
        plateletsError.setVisible(false);
        glucoseError.setVisible(false);
        meanCellVolumeError.setVisible(false);
        haematocritError.setVisible(false);
        meanCellHaematocritError.setVisible(false);
    }

    /**
     * check that all blood test properties are valid
     * @return returns true if all properties are valid
     */
    private boolean validateField() {
        boolean fieldValid = true;
        fieldValid &= bloodTestValidation(redBloodCount,redBloodCellError,BloodTestProperties.RBC);
        fieldValid &= bloodTestValidation(whiteBloodCount,whiteBloodCellError,BloodTestProperties.WBC);
        fieldValid &= bloodTestValidation(heamoglobin,heamoglobinError,BloodTestProperties.HAEMOGLOBIN);
        fieldValid &= bloodTestValidation(platelets,plateletsError,BloodTestProperties.PLATELETS);
        fieldValid &= bloodTestValidation(glucose,glucoseError,BloodTestProperties.GLUCOSE);
        fieldValid &= bloodTestValidation(meanCellVolume, meanCellVolumeError, BloodTestProperties.MEAN_CELL_VOLUME);
        fieldValid &= bloodTestValidation(haematocrit, haematocritError, BloodTestProperties.HAEMATOCRIT);
        fieldValid &= bloodTestValidation(meanCellHaematocrit, meanCellHaematocritError, BloodTestProperties.MEAN_CELL_HAEMATOCRIT);
        if(!AttributeValidation.validateDateBeforeTomorrow(testDate.getValue())){
            dateErrorLabel.setVisible(true);
            invalidateNode(testDate);
            fieldValid = false;
        }

        return fieldValid;

    }

    /**
     * a method to add a new blood test to a user
     */
    @FXML
    private void addBloodTest() {
        resetErrorLabels();
        testDate.getStyleClass().remove("invalid");
        if (validateField()) {
            if (atLeastOneValue) {
                logicController.addBloodTest(testDate.getValue(), redBloodCount.getText(), whiteBloodCount.getText(),
                        heamoglobin.getText(), platelets.getText(), glucose.getText(), meanCellVolume.getText(),
                        haematocrit.getText(), meanCellHaematocrit.getText());
            }
        } else {
            AlertWindowFactory.generateAlertWindow("you must have provided at least one blood field property.");
        }
    }




}
