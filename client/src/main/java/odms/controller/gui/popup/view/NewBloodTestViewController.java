package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.utils.AttributeValidation;
import odms.controller.gui.popup.logic.NewBloodTestLogicController;

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
    @FXML
    private Label noPropertyPopupErrorLabel;

    private NewBloodTestLogicController logicController;
    private boolean atLeastOneValue = false;

    /**
     * Initializes the pop-up to add a new blood test
     *
     * @param user  The user who the blood test is for
     * @param stage The new stage
     */
    public void init(User user, Stage stage) {
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

    /**
     * Closes the pop-up
     */
    @FXML
    public void cancel() {
        logicController.cancel();
    }

    /**
     * resets the error label to be hidden
     */
    private void resetErrorLabels() {
        noPropertyPopupErrorLabel.setVisible(false);
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
     * Checks that all blood test properties are valid are that at least one field is filled out.
     *
     * @return returns true if all properties are valid
     */
    private boolean validateField() {
        boolean fieldValid;
        fieldValid = bloodTestValidation(redBloodCount, redBloodCellError, BloodTestProperties.RED_BLOOD_CELL);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(whiteBloodCount, whiteBloodCellError, BloodTestProperties.WHITE_BLOOD_CELL);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(heamoglobin, heamoglobinError, BloodTestProperties.HAEMOGLOBIN);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(platelets, plateletsError, BloodTestProperties.PLATELETS);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(glucose, glucoseError, BloodTestProperties.GLUCOSE);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(meanCellVolume, meanCellVolumeError, BloodTestProperties.MEAN_CELL_VOLUME);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(haematocrit, haematocritError, BloodTestProperties.HAEMATOCRIT);
        atLeastOneValue |= isAssigned();
        fieldValid &= bloodTestValidation(meanCellHaematocrit, meanCellHaematocritError, BloodTestProperties.MEAN_CELL_HAEMATOCRIT);
        atLeastOneValue |= isAssigned();
        if (!AttributeValidation.validateDateBeforeTomorrow(testDate.getValue())) {
            dateErrorLabel.setVisible(true);
            invalidateNode(testDate);
            fieldValid = false;
        }
        return fieldValid;
    }


    /**
     * A method to add a new blood test to a user
     */
    @FXML
    private void addBloodTest() {
        resetErrorLabels();
        atLeastOneValue = false;
        testDate.getStyleClass().remove("invalid");
        if (validateField()) {
            if (atLeastOneValue) {
                logicController.addBloodTest(testDate.getValue(), redBloodCount.getText(), whiteBloodCount.getText(),
                        heamoglobin.getText(), platelets.getText(), glucose.getText(), meanCellVolume.getText(),
                        haematocrit.getText(), meanCellHaematocrit.getText());
            } else {
                noPropertyPopupErrorLabel.setVisible(true);
            }
        }
    }
}
