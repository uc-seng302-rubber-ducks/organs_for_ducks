package odms.controller.gui.popup.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;
import odms.controller.gui.panel.ProcedureTabController;
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
    @FXML
    private Label titleLabel;
    private Stage stage;
    private User user;
    private ProcedureTabController procedureTabController;
    private MedicalProcedure procedure;

    /**
     * initialises the controller
     *
     * @param procedure              procedure to be modified
     * @param stage                  stage to display on
     * @param currentUser            user related to the procedure
     * @param procedureTabController parent controller from which this was called
     */
    public void init(MedicalProcedure procedure, Stage stage, User currentUser, ProcedureTabController procedureTabController){
        this.stage = stage;
        this.user = currentUser;
        this.procedureTabController = procedureTabController;
        this.procedure = procedure;
        setupOrgans();
        titleLabel.setText("Edit Procedure");
        newProcedureConfirm.setText("Update");
        if(procedure != null){
            newProcedureConfirm.setText("Confirm");
            titleLabel.setText("Add New Procedure");
            showProcedureToEdit(procedure);
            procedureDateSelector.setValue(LocalDate.now());
        }
    }

    /**
     * sets up the affected organs listview with entries containing an organ and a checkbox
     */
    private void setupOrgans() {
        ObservableList<TextStringCheckBox> allOrgans = FXCollections.observableList(new ArrayList<>());
        for (Organs organ : Organs.values()) {
            allOrgans.add(new TextStringCheckBox(organ.toString()));
        }
        organsAffectedByProcedureListView.setItems(allOrgans);
    }

    /**
     * displays the selected procedure
     * @param procedure procedure to be displayed
     */
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


    /**
     * fired when cancel button is clicked
     */
    @FXML
    void cancelNewProcedure() {
        stage.close();
    }

    /**
     * fired when the confirm button is clicked
     */
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
        if(procedure == null) {
            procedure = new MedicalProcedure(procedureDate, procedureName,
                    descriptionTextArea.getText(), new ArrayList<>());
            user.addMedicalProcedure(procedure);
        } else {
            procedure.setProcedureDate(procedureDate);
            procedure.setSummary(procedureName);
            procedure.setDescription(descriptionTextArea.getText());
            procedure.setOrgansAffected(new ArrayList<>());
        }
        for (TextStringCheckBox cb : organsAffectedByProcedureListView.getItems()) {
            if(cb.isSelected()){
                procedure.addOrgan(Organs.fromString(cb.toString()));
            }

        }
        procedureTabController.updateProcedureTables(user);
        stage.close();
    }
}
