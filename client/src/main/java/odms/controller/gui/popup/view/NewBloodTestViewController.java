package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

    private NewBloodTestLogicController logicController;
    private Boolean valid = true;

    public void init(User user, Stage stage){
        this.logicController = new NewBloodTestLogicController(user,stage);
        dateErrorLabel.setVisible(false);
    }

    @FXML
    public void cancel() {
        logicController.cancel();


    }

    private Boolean validateField() {
        Boolean valid = true;
        AttributeValidation.validateDouble(redBloodCount.getText());
        AttributeValidation.validateDouble(whiteBloodCount.getText());
        AttributeValidation.validateDouble(heamoglobin.getText());
        AttributeValidation.validateDouble(platelets.getText());
        AttributeValidation.validateDouble(glucose.getText());
        AttributeValidation.validateDouble(meanCellVolume.getText());
        AttributeValidation.validateDouble(haematocrit.getText());
        AttributeValidation.validateDouble(meanCellHaematocrit.getText());
        return valid;

    }

    @FXML
    private void addBloodTest() {
        dateErrorLabel.setVisible(false);
        testDate.getStyleClass().remove("invalid");

        if (testDate.getValue() != null){
            logicController.addBloodTest(testDate.getValue(), redBloodCount.getText(),whiteBloodCount.getText(),
                    heamoglobin.getText(), platelets.getText(),glucose.getText(),meanCellVolume.getText(),
                    haematocrit.getText(),meanCellHaematocrit.getText());
        } else {
            dateErrorLabel.setVisible(true);

            testDate.getStyleClass().add("invalid");

        }

    }



}
