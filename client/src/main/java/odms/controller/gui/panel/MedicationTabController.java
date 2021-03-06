package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.Medication;
import odms.commons.utils.HttpRequester;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.MedicationsTimeController;
import odms.controller.gui.window.UserController;
import okhttp3.OkHttpClient;
import org.controlsfx.control.textfield.TextFields;
import utils.StageIconLoader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MedicationTabController {

    public static final String FOR_USER_NHI = " for User NHI: ";
    public static final String AS_IT_IS_EMPTY = " as it is empty";
    @FXML
    private ListView<String> previousMedicationListView;

    @FXML
    private ListView<String> currentMedicationListView;

    @FXML
    private Button untakeMedicationButton;

    @FXML
    private Button takeMedicationButton;

    @FXML
    private Button deleteMedicationButton;

    @FXML
    private TextField medicationTextField;

    @FXML
    private Button addMedicationButton;

    @FXML
    private TextArea drugDetailsTextArea;

    @FXML
    private Label drugDetailsLabel;

    @FXML
    private Label medicationLabel;

    private AppController application;
    private ObservableList<String> currentMeds;
    private ObservableList<String> previousMeds;
    private User currentUser;
    private HttpRequester httpRequester = new HttpRequester(new OkHttpClient());
    private UserController parent;

    /**
     * Gives the user view the application controller and hides all label and buttons that are not
     * needed on opening
     *
     * @param controller    the application controller
     * @param user          the current user
     * @param fromClinician boolean value indication if from clinician view
     * @param parent        Usercontroller window it is tied to
     */
    public void init(AppController controller, User user, boolean fromClinician, UserController parent) {

        application = controller;
        currentUser = user;
        this.parent = parent;
        //This is the place to set visible and invisible controls for Clinician vs User
        if (!fromClinician) {
            addMedicationButton.setVisible(false);
            medicationTextField.setVisible(false);
            deleteMedicationButton.setVisible(false);
            takeMedicationButton.setVisible(false);
            untakeMedicationButton.setVisible(false);
            medicationLabel.setVisible(false);
        }

        currentMeds = FXCollections.observableArrayList();

        previousMeds = FXCollections.observableArrayList();

        currentMedicationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        previousMedicationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //listeners to move meds from current <--> previous
        setListeners();
        setObservables();

    }

    private void setObservables() {
        //lambdas for drug interactions
        currentMedicationListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    ObservableList<String> selected = currentMedicationListView.getSelectionModel()
                            .getSelectedItems();
                    displayDetails(selected, drugDetailsLabel, drugDetailsTextArea);
                });
        previousMedicationListView.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    ObservableList<String> selected = previousMedicationListView.getSelectionModel()
                            .getSelectedItems();
                    displayDetails(selected, drugDetailsLabel, drugDetailsTextArea);
                }));
        currentMedicationListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String med = currentMedicationListView.getSelectionModel().getSelectedItem();
                launchMedicationView(med);
            }
        });
        previousMedicationListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String med = previousMedicationListView.getSelectionModel().getSelectedItem();
                launchMedicationView(med);
            }
        });
        medicationTextField.focusedProperty().addListener(
                (observable, oldValue, newValue) -> new Thread(this::getDrugSuggestions).start());
        medicationTextField.textProperty()
                .addListener(observable -> new Thread(this::getDrugSuggestions).start());

        medicationTextField.setOnMouseClicked(event -> new Thread(this::getDrugSuggestions));
        medicationTextField.textProperty()
                .addListener(observable -> new Thread(this::getDrugSuggestions));
    }

    private void setListeners() {
        previousMeds.addListener((ListChangeListener.Change<? extends String> change) -> {
            previousMedicationListView.setItems(previousMeds);
            application.update(currentUser);
        });
        currentMeds.addListener((ListChangeListener.Change<? extends String> change) -> {
            currentMedicationListView.setItems(currentMeds);
            application.update(currentUser);
        });
    }

    /**
     * Takes the information in the medication text fields and then calls the required API to get auto
     * complete information Which is then displayed. Should always be started on a new thread
     */
    private void getDrugSuggestions() {
        String newValue = medicationTextField.getText();
        if (newValue.length() > 1) {
            try {
                String autocompleteRaw = httpRequester.getSuggestedDrugs(newValue);
                String[] values = autocompleteRaw.replaceAll("^\"", "").replaceAll("\\[", "")
                        .replaceAll("]", "").split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replace('"', ' ').trim();
                }
                TextFields.bindAutoCompletion(medicationTextField, values);
                Log.info("Successfully loaded suggested medication names with user Input: " + newValue + FOR_USER_NHI + currentUser.getNhi());

            } catch (IOException e) {
                Log.severe("Failed to load suggested medication names with user Input: " + newValue + FOR_USER_NHI + currentUser.getNhi(), e);
            }

        }
    }

    public void refreshLists(User user) {
        currentUser = user;

        currentMeds.clear();
        if (!user.getCurrentMedication().isEmpty()) {
            currentMedicationListView.getItems().clear();
            List<String> medications = new ArrayList<>();
            for (Medication meds : user.getCurrentMedication()) {
                if (!meds.isDeleted()) {
                    medications.add(meds.getMedName());
                }
            }

            currentMeds.addAll(medications);

            currentMedicationListView.setItems(currentMeds);
        }

        previousMeds.clear();
        if (!user.getPreviousMedication().isEmpty()) {
            previousMedicationListView.getItems().clear();
            List<String> medications2 = new ArrayList<>();
            for (Medication meds : user.getPreviousMedication()) {
                if (!meds.isDeleted()) {
                    medications2.add(meds.getMedName());
                }
            }
            previousMeds.addAll(medications2);
            previousMedicationListView.setItems(previousMeds);
        }

        parent.refreshHistoryTable();
        currentMedicationListView.refresh();
        previousMedicationListView.refresh();
    }

    /**
     * Adds a medication to the current users profile that they are taking
     */
    @FXML
    void addMedication() {
        String medication = medicationTextField.getText();
        if (medication.isEmpty()) {
            Log.warning("Unable to add medication: " + medication + FOR_USER_NHI + currentUser.getNhi() + AS_IT_IS_EMPTY);
            return;
        }
        if (currentMeds.contains(medication) || previousMeds.contains(medication)) {
            medicationTextField.setText("");
            Log.info("Medication: " + medication + " already exist, updated GUI instead of adding new medication for User NHI: " + currentUser.getNhi());
            return;
        }
        currentUser.getRedoStack().clear();
        currentUser.saveStateForUndo();
        medicationTextField.setText("");
        currentMeds.add(medication);
        currentUser.addCurrentMedication(medication);
        parent.updateUndoRedoButtons();
        Log.info("Successfully added medication: " + medication + FOR_USER_NHI + currentUser.getNhi());

    }

    /**
     * Deletes a currently taking medication from the current users profile
     */
    @FXML
    void deleteMedication() {
        String medCurrent = currentMedicationListView.getSelectionModel().getSelectedItem();
        String medPrevious = previousMedicationListView.getSelectionModel().getSelectedItem();

        if (medCurrent != null) {
            currentUser.saveStateForUndo();
            parent.updateUndoRedoButtons();
            currentMeds.remove(medCurrent);
            currentUser.removeCurrentMedication(medCurrent);
            Log.info("Successfully deleted current medication: " + medCurrent + FOR_USER_NHI + currentUser.getNhi());
        } else {
            Log.warning("Unable to delete current medication : " + medCurrent + FOR_USER_NHI + currentUser.getNhi() + " because it is empty");
        }

        if (medPrevious != null) {
            currentUser.saveStateForUndo();
            parent.updateUndoRedoButtons();
            previousMeds.remove(medPrevious);
            currentUser.removePreviousMedication(medPrevious);
            Log.info("Successfully deleted previous medication: " + previousMeds + FOR_USER_NHI + currentUser.getNhi());
        } else {
            Log.warning("Unable to delete previous medication: " + previousMeds + FOR_USER_NHI + currentUser.getNhi() + " because it is empty");
        }
    }

    /**
     * fires when a medication is moved from previous to current medications
     */
    @FXML
    void takeMedication() {
        String med = previousMedicationListView.getSelectionModel().getSelectedItem();
        if (med == null) {
            Log.warning("Unable to take medication for User NHI: " + currentUser.getNhi() + AS_IT_IS_EMPTY);
            return;
        }
        currentUser.saveStateForUndo();
        parent.updateUndoRedoButtons();
        if (currentMeds.contains(med)) {
            currentUser.removePreviousMedication(med);
            previousMeds.remove(med);
            Log.info("Successfully removed previous medication: " + med + FOR_USER_NHI + currentUser.getNhi());
            return;
        }
        currentMeds.add(med);
        currentUser.addCurrentMedication(med);
        previousMeds.remove(med);
        currentUser.removePreviousMedication(med);
        Log.info("Successfully moved medication: " + med + " from previous to current medication for User NHI: " + currentUser.getNhi());
    }

    /**
     * fires when a medication is moved from current to previous
     */
    @FXML
    void untakeMedication() {
        String med = currentMedicationListView.getSelectionModel().getSelectedItem();
        if (med == null) {
            Log.warning("Unable to un-take medication for User NHI: " + currentUser.getNhi() + AS_IT_IS_EMPTY);
            return;
        }
        currentUser.saveStateForUndo();
        parent.updateUndoRedoButtons();
        if (previousMeds.contains(med)) {
            currentUser.removeCurrentMedication(med);
            currentMeds.remove(med);
            Log.info("Successfully un-take previous medication: " + med + FOR_USER_NHI + currentUser.getNhi());
            return;
        }
        currentUser.removeCurrentMedication(med);
        currentMeds.remove(med);
        previousMeds.add(med);
        currentUser.addPreviousMedication(med);
        currentUser.getPreviousMedicationTimes(med).add(LocalDateTime.now());
        Log.info("Successfully moved medication: " + med + " from current to previous medication for User NHI: " + currentUser.getNhi());
    }

    /**
     * Removes the highlight of the currently selected medication
     */
    @FXML
    void clearCurrentMedSelection() {
        currentMedicationListView.getSelectionModel().clearSelection();
    }

    /**
     * Removes the highlight of the previously selected medication
     */
    @FXML
    void clearPreviousMedSelection() {
        previousMedicationListView.getSelectionModel().clearSelection();
    }

    /**
     * Opens the selected medication in a new window with additional information
     *
     * @param med A string of medication
     */
    private void launchMedicationView(String med) {
        FXMLLoader medicationTimeViewLoader = new FXMLLoader(
                getClass().getResource("/FXML/medicationsTimeView.fxml"));
        Parent root;
        try {
            root = medicationTimeViewLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            MedicationsTimeController medicationsTimeController = medicationTimeViewLoader
                    .getController();
            medicationsTimeController.init(currentUser, stage, med);
            StageIconLoader stageIconLoader = new StageIconLoader();
            stage = stageIconLoader.addStageIcon(stage);
            stage.show();
            Log.info("successfully launched Medications Time view window for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("Failed to launch Medications Time view window for User NHI: " + currentUser.getNhi(), e);
        }


    }

    /**
     * takes selected items from lambda functions. handles http requesting and displaying results if
     * one item is selected, active ingredients will be shown. If two are selected, the interactions
     * between the two will be displayed
     *
     * @param selected selected items from listview
     */
    private void displayDetails(ObservableList<String> selected, Label drugDetailsLabel,
                                TextArea drugDetailsTextArea) {
        if (selected.size() > 2) {
            drugDetailsLabel.setText("Drug Details");
            drugDetailsTextArea.setText(
                    "Please select any two drugs from either previous or current medications to view the interactions between them\n"
                            + "or select one drug to see it's active ingredients");
            return;
        }

        try {
            //active ingredients
            if (selected.size() == 1) {
                String[] res = httpRequester.getActiveIngredients(selected.get(0));
                StringBuilder sb = new StringBuilder();
                for (String ingredient : res) {
                    sb.append(ingredient);
                    sb.append("\n");
                }
                drugDetailsLabel.setText("Drug Details: Active Ingredients");
                if (sb.toString().equals("")) {
                    drugDetailsTextArea.setText("Could not find active ingredients");
                    return;
                }
                drugDetailsTextArea.setText(sb.toString());

            } else /*interactions*/ {
                Set<String> res = httpRequester
                        .getDrugInteractions(selected.get(0), selected.get(1), currentUser.getBirthGender(),
                                currentUser.getAge());
                StringBuilder sb = new StringBuilder();
                for (String symptom : res) {
                    sb.append(symptom);
                    sb.append("\n");
                }
                drugDetailsLabel.setText("Drug Details: Drug Interactions");
                if (sb.toString().equals("")) {
                    drugDetailsTextArea.setText("No interactions");
                    return;
                }
                drugDetailsTextArea.setText(sb.toString());
            }
            Log.info("Successfully loaded medication names from API call for User NHI: " + currentUser.getNhi());
        } catch (IOException ex) {
            //TODO display connectivity error message
            Log.severe("Failed to load medication names from API call for User NHI: " + currentUser.getNhi(), ex);
        }

    }

    public void clearPreviousMeds() {
        previousMeds.clear();
    }

}
