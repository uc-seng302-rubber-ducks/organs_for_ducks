package seng302.Controller;

import java.io.IOException;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import org.controlsfx.control.textfield.TextFields;
import seng302.Model.HttpRequester;
import seng302.Model.Memento;
import seng302.Model.User;

public class MedicationTabController {

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
  private Stage stage;
  private AppController application;
  private ObservableList<String> currentMeds;
  private ObservableList<String> previousMeds;
  private User currentUser;
  private OkHttpClient client = new OkHttpClient();

  /**
   * Gives the donor view the application controller and hides all label and buttons that are not
   * needed on opening
   *
   * @param controller the application controller
   * @param user the current user
   * @param stage the application stage
   * @param fromClinician boolean value indication if from clinician view
   */
  public void init(AppController controller, User user, Stage stage, boolean fromClinician) {
    this.stage = stage;
    application = controller;
    currentUser = user;
    //ageValue.setText("");
    //This is the place to set visable and invisable controls for Clinician vs User
    if (!fromClinician) {
      addMedicationButton.setVisible(false);
      medicationTextField.setVisible(false);
      deleteMedicationButton.setVisible(false);

      takeMedicationButton.setVisible(false);
      untakeMedicationButton.setVisible(false);
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
        .addListener((observable) -> new Thread(this::getDrugSuggestions).start());

    medicationTextField.setOnMouseClicked(event -> new Thread(this::getDrugSuggestions));
    medicationTextField.textProperty()
        .addListener((observable) -> new Thread(this::getDrugSuggestions));
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
        String autocompleteRaw = HttpRequester.getSuggestedDrugs(newValue, new OkHttpClient());
        String[] values = autocompleteRaw.replaceAll("^\"", "").replaceAll("\\[", "")
            .replaceAll("]", "").split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
        for (int i = 0; i < values.length; i++) {
          values[i] = values[i].replace('"', ' ').trim();
        }
        TextFields.bindAutoCompletion(medicationTextField, values);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  public void refreshLists(User user) {
    if (user.getCurrentMedication() != null) {
      currentMeds.clear();
      currentMedicationListView.getItems().clear();
      currentMeds.addAll(user.getCurrentMedication());

      currentMedicationListView.setItems(currentMeds);
    }

    if (user.getPreviousMedication() != null) {
      previousMeds.clear();
      previousMeds.addAll(user.getPreviousMedication());
      previousMedicationListView.setItems(previousMeds);
    }
  }

  /**
   * Adds a medication to the current users profile that they are taking
   *
   * @param event An action event
   */
  @FXML
  void addMedication(ActionEvent event) {
    String medication = medicationTextField.getText();
    if (medication.isEmpty()) {
      return;
    }
    if (currentMeds.contains(medication) || previousMeds.contains(medication)) {
      medicationTextField.setText("");
      return;
    }
    medicationTextField.setText("");
    currentMeds.add(medication);
    currentUser.addCurrentMedication(medication);


  }

  /**
   * Deletes a currently taking medication from the current users profile
   *
   * @param event An action event
   */
  @FXML
  void deleteMedication(ActionEvent event) {
    String medCurrent = currentMedicationListView.getSelectionModel().getSelectedItem();
    String medPrevious = previousMedicationListView.getSelectionModel().getSelectedItem();

    if (medCurrent != null) {
      currentMeds.remove(medCurrent);
      currentUser.removeCurrentMedication(medCurrent);
    }
    if (medPrevious != null) {
      previousMeds.remove(medPrevious);
      currentUser.removePreviousMedication(medPrevious);
    }
  }

  /**
   * fires when a medication is moved from previous to current medications
   *
   * @param event An action event
   */
  @FXML
  void takeMedication(ActionEvent event) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    String med = previousMedicationListView.getSelectionModel().getSelectedItem();
    if (med == null) {
      return;
    }
    if (currentMeds.contains(med)) {
      currentUser.removePreviousMedication(med);
      previousMeds.remove(med);
      return;
    }
    currentMeds.add(med);
    currentUser.addCurrentMedication(med);
    previousMeds.remove(med);
    currentUser.removePreviousMedication(med);
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);

  }

  /**
   * fires when a medication is moved from current to previous
   *
   * @param event An action event
   */
  @FXML
  void untakeMedication(ActionEvent event) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    String med = currentMedicationListView.getSelectionModel().getSelectedItem();
    if (med == null) {
      return;
    }
    if (previousMeds.contains(med)) {
      currentUser.removeCurrentMedication(med);
      currentMeds.remove(med);
      return;
    }
    currentUser.removeCurrentMedication(med);
    currentMeds.remove(med);
    previousMeds.add(med);
    currentUser.addPreviousMedication(med);
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);
  }

  /**
   * Removes the highlight of the currently selected medication
   *
   * @param event A mouse event
   */
  @FXML
  void clearCurrentMedSelection(MouseEvent event) {
    currentMedicationListView.getSelectionModel().clearSelection();
  }

  /**
   * Removes the highlight of the previously selected medication
   *
   * @param event A mouse event
   */
  @FXML
  void clearPreviousMedSelection(MouseEvent event) {
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
    Parent root = null;
    try {
      root = medicationTimeViewLoader.load();
      Stage stage = new Stage();
      stage.setScene(new Scene(root));
      MedicationsTimeController medicationsTimeController = medicationTimeViewLoader
          .getController();
      medicationsTimeController.init(application, currentUser, stage, med);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
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
        String[] res = HttpRequester.getActiveIngredients(selected.get(0), client);
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
        Set<String> res = HttpRequester
            .getDrugInteractions(selected.get(0), selected.get(1), currentUser.getGender(),
                currentUser.getAge(), client);
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
    } catch (IOException ex) {
      //TODO display connectivity error message
      System.out.println("oof");
    }

  }

  public void clearPreviousMeds() {
    previousMeds.clear();
  }

}