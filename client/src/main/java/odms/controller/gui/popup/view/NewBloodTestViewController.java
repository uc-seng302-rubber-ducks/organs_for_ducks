package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.Node;
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

import java.text.DecimalFormat;


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
    private BloodTestBridge bloodTestBridge;



    private NewBloodTestLogicController logicController;
    private Boolean valid = true;

    public void init(User user, Stage stage, BloodTestBridge bloodTestBridge){
        this.bloodTestBridge = bloodTestBridge;
        this.logicController = new NewBloodTestLogicController(user,stage);
        resetErrorLabels();
    }

    @FXML
    public void cancel() {
        logicController.cancel();


    }

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

    private Boolean BloodTestValidation(TextField textField, Label label, BloodTestProperties bloodTestProperties){
        valid = true;
        DecimalFormat df2 = new DecimalFormat(".##");
        double value = AttributeValidation.validateDouble(textField.getText());
        if (value == -1){
            label.setVisible(true);
            invalidateNode(textField);
            valid = false;
        } else if (value > (bloodTestProperties.getUpperBound()) * 5.0){
            label.setText("that number is too large the max number is " + df2.format(bloodTestProperties.getUpperBound() * 5.0));
            label.setVisible(true);
            invalidateNode(textField);
            valid = false;
        } else if (value < (bloodTestProperties.getLowerBound() / 5.0)) {
            label.setText("that number is too small the min number is " + df2.format(bloodTestProperties.getLowerBound() / 5.0));
            label.setVisible(true);
            invalidateNode(textField);
            valid = false;
        }
        return valid;
    }

    private boolean validateField() {
        boolean fieldValid = true;
        fieldValid &= BloodTestValidation(redBloodCount,redBloodCellError,BloodTestProperties.RBC);
        fieldValid &= BloodTestValidation(whiteBloodCount,whiteBloodCellError,BloodTestProperties.WBC);
        fieldValid &= BloodTestValidation(heamoglobin,heamoglobinError,BloodTestProperties.HAEMOGLOBIN);
        fieldValid &= BloodTestValidation(platelets,plateletsError,BloodTestProperties.PLATELETS);
        fieldValid &= BloodTestValidation(glucose,glucoseError,BloodTestProperties.GLUCOSE);
        fieldValid &= BloodTestValidation(meanCellVolume, meanCellVolumeError, BloodTestProperties.MEAN_CELL_VOLUME);
        fieldValid &= BloodTestValidation(haematocrit, haematocritError, BloodTestProperties.HAEMATOCRIT);
        fieldValid &= BloodTestValidation(meanCellHaematocrit, meanCellHaematocritError, BloodTestProperties.MEAN_CELL_HAEMATOCRIT);
        if(!AttributeValidation.validateDateBeforeTomorrow(testDate.getValue())){
            dateErrorLabel.setVisible(true);
            invalidateNode(testDate);
            fieldValid = false;
        }
        return fieldValid;

    }

    @FXML
    private void addBloodTest() {
        resetErrorLabels();
        testDate.getStyleClass().remove("invalid");
        if (validateField()) {
            logicController.addBloodTest(testDate.getValue(), redBloodCount.getText(),whiteBloodCount.getText(),
                    heamoglobin.getText(), platelets.getText(),glucose.getText(),meanCellVolume.getText(),
                    haematocrit.getText(),meanCellHaematocrit.getText());
        }
    }

    private void invalidateNode(Node node) {
        node.getStyleClass().add("invalid");
    }



}
