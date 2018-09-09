package odms.controller.gui.popup.view;

import com.sun.istack.internal.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;
import odms.controller.gui.widget.TextStringCheckBox;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProcedureModificationViewController {

    public static final String FAILED_TO_ADD_PROCEDURE = "Failed to add procedure: ";
    public static final String FOR_USER_NHI = " for User NHI: ";
    public static final String AS_USER_INPUT_IS_INVALID = " as user input is invalid";
    public static final String FAILED_TO_UPDATE_PROCEDURE = "Failed to update procedure: ";
    @FXML
    private DatePicker procedureDateSelector;
    @FXML
    private ListView<TextStringCheckBox> organsAffectedByProcedureListView;
    @FXML
    private Button newProcedureCancel;
    @FXML
    private Label warningLabel;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField procedureTextField;
    @FXML
    private Button newProcedureConfirm;
    private Stage stage;
    private User user;

    public void init(@Nullable MedicalProcedure procedure, Stage stage, User currentUser){
        this.stage = stage;
        this.user = currentUser;
        setupOrgans();
        if(procedure != null){
            showProcedureToEdit(procedure);
        }
    }

    private void setupOrgans() {
        ObservableList<TextStringCheckBox> allOrgans = FXCollections.observableList(new ArrayList<>());
        for (Organs organ : Organs.values()) {
            allOrgans.add(new TextStringCheckBox(organ.toString()));
        }
        organsAffectedByProcedureListView.setItems(allOrgans);
    }

    private void showProcedureToEdit(MedicalProcedure procedure) {
        procedureTextField.setText(procedure.getSummary());
        procedureDateSelector.setValue(procedure.getProcedureDate());
        descriptionTextArea.setText(procedure.getDescription());
        for(TextStringCheckBox organ : organsAffectedByProcedureListView.getItems()){
            if(procedure.getOrgansAffected().contains(Organs.fromString(organ.toString()))){
                organ.setSelected(true);
            }
        }
    }


    @FXML
    void cancelNewProcedure() {
        stage.close();
    }

    @FXML
    void addNewProcedure() {
        String procedureName = procedureTextField.getText();
        if (procedureName.isEmpty()) {
            Log.warning(FAILED_TO_ADD_PROCEDURE + procedureName + FOR_USER_NHI + user.getNhi() + AS_USER_INPUT_IS_INVALID);
            warningLabel.setText("A name must be entered for a procedure");
            return;
        }
        LocalDate procedureDate = procedureDateSelector.getValue();
        if (procedureDate == null) {
            Log.warning(FAILED_TO_ADD_PROCEDURE + procedureName + FOR_USER_NHI + user.getNhi() + AS_USER_INPUT_IS_INVALID);
            warningLabel.setText("A valid date must be entered for a procedure");
            return;
        }
        if (procedureDate.isBefore(user.getDateOfBirth())) {
            Log.warning(FAILED_TO_ADD_PROCEDURE + procedureName + FOR_USER_NHI + user.getNhi() + AS_USER_INPUT_IS_INVALID);
            warningLabel.setText("Procedures may not occur before a patient has been born");
            return;
        }
        user.saveStateForUndo();

        MedicalProcedure procedure = new MedicalProcedure(procedureDate, procedureName,
                descriptionTextArea.getText(), new ArrayList<>());
        for (TextStringCheckBox cb : organsAffectedByProcedureListView.getItems()) {
            if(cb.isSelected()){
                procedure.addOrgan(Organs.fromString(cb.toString()));
            }

        }
        user.addMedicalProcedure(procedure);
        stage.close();
    }
}
