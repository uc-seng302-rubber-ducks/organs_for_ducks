package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.commons.model.Change;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.OrgansAffectedController;
import odms.controller.gui.widget.TextStringCheckBox;
import odms.controller.gui.window.UserController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcedureTabController {
    public static final String FAILED_TO_ADD_PROCEDURE = "Failed to add procedure: ";
    public static final String FOR_USER_NHI = " for User NHI: ";
    public static final String AS_USER_INPUT_IS_INVALID = " as user input is invalid";
    public static final String FAILED_TO_UPDATE_PROCEDURE = "Failed to update procedure: ";
    //procedures

    @FXML
    private Button removeProcedureButton;

    @FXML
    private Button addProcedureButton;

    @FXML
    private Button updateProceduresButton;

    @FXML
    private Button modifyOrgansProcedureButton;

    @FXML
    private DatePicker procedureDateSelector;

    @FXML
    private TextField procedureTextField;

    @FXML
    private Label procedureWarningLabel;

    @FXML
    private TableView<MedicalProcedure> previousProcedureTableView;

    @FXML
    private TableView<MedicalProcedure> pendingProcedureTableView;

    @FXML
    private ListView<TextStringCheckBox> organsAffectedByProcedureListView;

    @FXML
    private TextArea descriptionTextArea;

    private TableView<MedicalProcedure> currentProcedureList;
    private ObservableList<MedicalProcedure> medicalProcedures;
    private ObservableList<MedicalProcedure> previousProcedures;
    private ObservableList<MedicalProcedure> pendingProcedures;

    private User currentUser;
    private AppController application;
    private UserController parent;

    /**
     * Gives the user view the application controller and hides all label and buttons that are not
     * needed on opening
     *
     * @param controller    the application controller
     * @param user          the current user
     * @param fromClinician boolean value indication if from clinician view
     * @param parent        the UserController class this belongs to
     */
    public void init(AppController controller, User user, boolean fromClinician,
                     UserController parent) {
        application = controller;
        currentUser = user;
        this.parent = parent;

        procedureWarningLabel.setText("");
        procedureDateSelector.setValue(LocalDate.now());
        previousProcedures = FXCollections.observableArrayList();
        pendingProcedures = FXCollections.observableArrayList();
        pendingProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        previousProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        moveSelectedProcedureTo(previousProcedureTableView, pendingProcedureTableView);
        moveSelectedProcedureTo(pendingProcedureTableView, previousProcedureTableView);
        descriptionTextArea.setWrapText(true);
        constructTables();
        if (!fromClinician) {
            procedureDateSelector.setEditable(false);
            procedureTextField.setEditable(false);
            descriptionTextArea.setEditable(false);
            addProcedureButton.setVisible(false);
            removeProcedureButton.setVisible(false);
            updateProceduresButton.setVisible(false);
            modifyOrgansProcedureButton.setVisible(false);
            List<TextStringCheckBox> organs = organsAffectedByProcedureListView.getItems();
            for(TextStringCheckBox organ : organs){
                organ.setDisable(true);
            }
        }

        modifyOrgansProcedureButton.setVisible(false);
    }

    private void constructTables() {
        TableColumn pendingProcedureColumn = new TableColumn("Procedure");
        TableColumn pendingDateColumn = new TableColumn("Date");
        TableColumn previousProcedureColumn = new TableColumn("Procedure");
        TableColumn previousDateColumn = new TableColumn("Date");
        pendingProcedureColumn
                .setCellValueFactory(new PropertyValueFactory<MedicalProcedure, String>("summary"));
        previousProcedureColumn
                .setCellValueFactory(new PropertyValueFactory<MedicalProcedure, String>("summary"));
        pendingDateColumn
                .setCellValueFactory(new PropertyValueFactory<Change, String>("procedureDate"));
        previousDateColumn
                .setCellValueFactory(new PropertyValueFactory<Change, String>("procedureDate"));
        previousProcedureTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pendingProcedureTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        previousProcedureTableView.getColumns().addAll(previousProcedureColumn, previousDateColumn);
        pendingProcedureTableView.getColumns().addAll(pendingProcedureColumn, pendingDateColumn);
        ObservableList<TextStringCheckBox> allOrgans = FXCollections.observableList(new ArrayList<>());
        for(Organs organ : Organs.values()){
            allOrgans.add(new TextStringCheckBox(organ.toString()));
        }
        organsAffectedByProcedureListView.setItems(allOrgans);
/*        organsAffectedByProcedureListView.setCellFactory(oabp -> {
            TextFieldListCell<Organs> cell = new TextFieldListCell<>();
            cell.setConverter(new StringConverter<Organs>() {
                @Override
                public String toString(Organs object) {
                    return object.toString();
                }

                @Override
                public Organs fromString(String string) {
                    return null;
                }
            });
            return cell;
        });*/
    }

    /**
     * A method to add a listener to the from TableView to unselect from one list and show procedure
     * from the appropriate list
     *
     * @param from a TableView object holding medical procedures
     * @param to   a TableView object to deselect from
     */
    private void moveSelectedProcedureTo(TableView<MedicalProcedure> from,
                                         TableView<MedicalProcedure> to) {
        from.getSelectionModel().selectedItemProperty()
                .addListener(listChangeListener -> {
                    to.getSelectionModel().select(null);
                    if (from.getSelectionModel().getSelectedItem() != null) {
                        showProcedure(from.getSelectionModel().getSelectedItem());
                        modifyOrgansProcedureButton.setVisible(true);
                        currentProcedureList = from;
                    }
                });
    }

    /**
     * Updates the procedure tables and ensure that the selected item is not changed.
     */
    public void updateProcedureTables(User user) {
        currentUser = user;

        boolean pendingProceduresTableSelected = (
                pendingProcedureTableView.getSelectionModel().getSelectedItem() != null);

        int index = pendingProceduresTableSelected ? pendingProcedureTableView.getSelectionModel()
                .getSelectedIndex() : previousProcedureTableView.getSelectionModel().getSelectedIndex();

        previousProcedures.clear();
        pendingProcedures.clear();
        medicalProcedures = FXCollections.observableList(user.getMedicalProcedures().stream().filter(p -> !p.isDeleted()).collect(Collectors.toList()));

        for (MedicalProcedure procedure : medicalProcedures) {
            if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
                previousProcedures.add(procedure);
            } else {
                pendingProcedures.add(procedure);
            }
        }
        parent.refreshHistoryTable();

        previousProcedureTableView.setItems(previousProcedures);
        pendingProcedureTableView.setItems(pendingProcedures);

        if (pendingProceduresTableSelected) {
            try {
                pendingProcedureTableView.getSelectionModel().select(index);
            } catch (IndexOutOfBoundsException e) {
                previousProcedureTableView.getSelectionModel().select(previousProcedures.size() - 1);
            }
        } else {
            try {
                previousProcedureTableView.getSelectionModel().select(index);
            } catch (IndexOutOfBoundsException e) {
                pendingProcedureTableView.getSelectionModel().select(pendingProcedures.size() - 1);
            }
        }
        previousProcedureTableView.refresh();
        pendingProcedureTableView.refresh();
    }

    /**
     * Adds a procedure to the current user when a procedure name is entered
     */
    @FXML
    void addProcedure() {
        String procedureName = procedureTextField.getText();
        if (procedureName.isEmpty()) {
            Log.warning(FAILED_TO_ADD_PROCEDURE + procedureName + FOR_USER_NHI + currentUser.getNhi() + AS_USER_INPUT_IS_INVALID);
            procedureWarningLabel.setText("A name must be entered for a procedure");
            return;
        }
        LocalDate procedureDate = procedureDateSelector.getValue();
        if (procedureDate == null) {
            Log.warning(FAILED_TO_ADD_PROCEDURE + procedureName + FOR_USER_NHI + currentUser.getNhi() + AS_USER_INPUT_IS_INVALID);
            procedureWarningLabel.setText("A valid date must be entered for a procedure");
            return;
        }
        if (procedureDate.isBefore(currentUser.getDateOfBirth())) {
            Log.warning(FAILED_TO_ADD_PROCEDURE + procedureName + FOR_USER_NHI + currentUser.getNhi() + AS_USER_INPUT_IS_INVALID);
            procedureWarningLabel.setText("Procedures may not occur before a patient has been born");
            return;
        }
        currentUser.saveStateForUndo();

        MedicalProcedure procedure = new MedicalProcedure(procedureDate, procedureName,
                descriptionTextArea.getText(), new ArrayList<>());
        medicalProcedures.add(procedure);
        currentUser.addMedicalProcedure(procedure);
        if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
            previousProcedures.add(procedure);
        } else {
            pendingProcedures.add(procedure);
        }
        clearProcedure();
        application.update(currentUser);
        parent.updateUndoRedoButtons();
        Log.info("Successfully added procedure: " + procedureName + FOR_USER_NHI + currentUser.getNhi());
    }

    /**
     * Updates an existing procedures information
     */
    @FXML
    void updateProcedures() {
        procedureWarningLabel.setText("");
        String newName = procedureTextField.getText();
        LocalDate newDate = procedureDateSelector.getValue();
        String newDescription = descriptionTextArea.getText();
        if (newName.isEmpty()) {
            Log.warning(FAILED_TO_UPDATE_PROCEDURE + newName + FOR_USER_NHI + currentUser.getNhi() + AS_USER_INPUT_IS_INVALID);
            procedureWarningLabel.setText("A name must be entered for a procedure");
            return;
        }
        if (newDate == null) {
            Log.warning(FAILED_TO_UPDATE_PROCEDURE + newName + FOR_USER_NHI + currentUser.getNhi() + AS_USER_INPUT_IS_INVALID);
            procedureWarningLabel.setText("A valid date must be entered for a procedure");
            return;
        }
        if (newDate.isBefore(currentUser.getDateOfBirth())) {
            Log.warning(FAILED_TO_UPDATE_PROCEDURE + newName + FOR_USER_NHI + currentUser.getNhi() + AS_USER_INPUT_IS_INVALID);
            procedureWarningLabel.setText("Procedures may not occur before a patient has been born");
            return;
        }
        currentUser.saveStateForUndo();
        currentUser.getRedoStack().clear();

        if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            MedicalProcedure procedure = previousProcedureTableView.getSelectionModel().getSelectedItem();

            updateProcedure(procedure, newName, newDate, newDescription);
        } else if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            MedicalProcedure procedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();

            updateProcedure(procedure, newName, newDate, newDescription);
        } else {
            currentUser.getUndoStack().pop(); // no procedure was selected to be updated so the last undo has no changes
        }

        parent.updateUndoRedoButtons();
        Log.info("Successfully updated procedure: " + newName + FOR_USER_NHI + currentUser.getNhi());
    }

    /**
     * * Helper function for the updateProcedures button. Takes a procedure and updates it
     *
     * @param procedure      procedure to be updated
     * @param newName        new procedure name
     * @param newDate        new procedure date
     * @param newDescription new procedure description
     */
    private void updateProcedure(MedicalProcedure procedure, String newName, LocalDate newDate,
                                 String newDescription) {
        procedure.setSummary(newName);
        procedure.setDescription(newDescription);
        LocalDate oldDate = procedure.getProcedureDate();
        if (newDate != null) {
            procedure.setProcedureDate(newDate);
        }
        if (procedure.getProcedureDate().isBefore(LocalDate.now()) && !oldDate
                .isBefore(LocalDate.now())) {
            pendingProcedures.remove(procedure);
            previousProcedures.add(procedure);
        } else if (procedure.getProcedureDate().isAfter(LocalDate.now()) && !oldDate
                .isAfter(LocalDate.now())) {
            previousProcedures.remove(procedure);
            pendingProcedures.add(procedure);
        }
        application.update(currentUser);
    }

    /**
     * Shows all the information for a given procedure
     *
     * @param procedure current medical procedure
     */
    private void showProcedure(MedicalProcedure procedure) {
        procedureTextField.setText(procedure.getSummary());
        procedureDateSelector.setValue(procedure.getProcedureDate());
        descriptionTextArea.setText(procedure.getDescription());

        List<Organs> organsAffected = procedure.getOrgansAffected();
        List<TextStringCheckBox> listOrgans = organsAffectedByProcedureListView.getItems();
        for(TextStringCheckBox organCheck : listOrgans){
            organCheck.setSelected(false);
            if(organsAffected.contains(Organs.valueOf(organCheck.toString().toUpperCase().replaceAll(" ", "_")))){
                organCheck.setSelected(true);
            }
        }

        parent.updateUndoRedoButtons();
        pendingProcedureTableView.refresh();
        previousProcedureTableView.refresh();
    }

    /**
     * Clears the information of a shown procedure
     */
    @FXML
    void clearProcedure() {
        procedureWarningLabel.setText("");
        procedureTextField.setText("");
        procedureDateSelector.setValue(LocalDate.now());
        descriptionTextArea.setText("");
        pendingProcedureTableView.getSelectionModel().select(null);
        previousProcedureTableView.getSelectionModel().select(null);
        organsAffectedByProcedureListView.setItems(FXCollections.observableList(new ArrayList<>()));
        modifyOrgansProcedureButton.setVisible(false);
        currentProcedureList = null;
        Log.info("Successfully cleared procedure for User NHI: " + currentUser.getNhi());
    }

    /**
     * Removes a procedure from the curernt users profile
     */
    @FXML
    void removeProcedure() {
        currentUser.saveStateForUndo();
        if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            previousProcedureTableView.getSelectionModel().getSelectedItem().setDeleted(true);
            Log.info("Successfully removed procedure: " + previousProcedureTableView.getSelectionModel().getSelectedItem().toString() + FOR_USER_NHI + currentUser.getNhi());
            previousProcedures.remove(previousProcedureTableView.getSelectionModel().getSelectedItem());
        } else if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            pendingProcedureTableView.getSelectionModel().getSelectedItem().setDeleted(true);
            Log.info("Successfully removed procedure: " + pendingProcedureTableView.getSelectionModel().getSelectedItem().toString() + FOR_USER_NHI + currentUser.getNhi());
            pendingProcedures.remove(pendingProcedureTableView.getSelectionModel().getSelectedItem());
        } else {
            currentUser.getUndoStack().pop();
            Log.warning("Failed to remove procedure for User NHI: " + currentUser.getNhi() + " as no procedure is selected");
        }
        currentUser.getRedoStack().clear();

        application.update(currentUser);
        parent.updateUndoRedoButtons();
    }

    /**
     * Opens the modify procedure organs window for the selected procedure
     */
    @FXML
    void modifyProcedureOrgans() {
        currentUser.saveStateForUndo();
        currentUser.getRedoStack().clear();

        MedicalProcedure procedure = currentProcedureList.getSelectionModel().getSelectedItem();
        FXMLLoader affectedOrganLoader = new FXMLLoader(
                getClass().getResource("/FXML/organsAffectedView.fxml"));
        Parent root;
        try {
            root = affectedOrganLoader.load();
            Stage s = new Stage();
            s.setScene(new Scene(root));
            s.initModality(Modality.APPLICATION_MODAL);
            OrgansAffectedController organsAffectedController = affectedOrganLoader.getController();
            organsAffectedController.init(application, s, procedure, currentUser);
            s.showAndWait();
            showProcedure(procedure);
            Log.info("Successfully launched Modify Procedure Organs window for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("unable to launch Modify Procedure Organs window for User NHI: " + currentUser.getNhi(), e);
        }
        parent.updateUndoRedoButtons();
    }
}
