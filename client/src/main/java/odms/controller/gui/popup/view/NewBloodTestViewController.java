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
import odms.commons.utils.AttributeValidation;
import odms.controller.gui.popup.logic.NewBloodTestLogicController;


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

    private boolean validateField() {
        boolean valid = true;
        if (AttributeValidation.validateDouble(redBloodCount.getText()) == -1) {
            redBloodCellError.setVisible(true);
            invalidateNode(redBloodCount);
            valid = false;
        }
        if (AttributeValidation.validateDouble(whiteBloodCount.getText()) == -1){
            whiteBloodCellError.setVisible(true);
            invalidateNode(whiteBloodCount);
            valid = false;

        }
        if (AttributeValidation.validateDouble(heamoglobin.getText()) == -1) {
            heamoglobinError.setVisible(true);
            invalidateNode(heamoglobin);
            valid = false;
        }
        if (AttributeValidation.validateDouble(platelets.getText()) == -1) {
            plateletsError.setVisible(true);
            invalidateNode(platelets);
            valid = false;
        }
        if (AttributeValidation.validateDouble(glucose.getText()) == -1) {
            glucoseError.setVisible(true);
            invalidateNode(glucose);
            valid = false;
        }
        if (AttributeValidation.validateDouble(meanCellVolume.getText()) == -1) {
            meanCellVolumeError.setVisible(true);
            invalidateNode(meanCellVolume);
            valid = false;
        }
        if (AttributeValidation.validateDouble(haematocrit.getText()) == -1) {
            haematocritError.setVisible(true);
            invalidateNode(haematocrit);
            valid = false;
        }
        if (AttributeValidation.validateDouble(meanCellHaematocrit.getText()) == -1){
            meanCellHaematocritError.setVisible(true);
            invalidateNode(meanCellHaematocrit);
            valid = false;
        }
        if(!AttributeValidation.validateDateBeforeTomorrow(testDate.getValue())){
            dateErrorLabel.setVisible(true);
            invalidateNode(testDate);
            valid = false;
        }
        return valid;

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
