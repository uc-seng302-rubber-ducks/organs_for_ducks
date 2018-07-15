package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import odms.controller.AppController;
import odms.controller.gui.popup.NewDiseaseController;
import odms.controller.gui.window.UserController;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.utils.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

public class DiseasesTabPageController {

    @FXML
    private TableView<Disease> currentDiseaseTableView;

    @FXML
    private TableView<Disease> pastDiseaseTableView;

    @FXML
    private Button addDiseaseButton;

    @FXML
    private Button updateDiseaseButton;

    @FXML
    private Button deleteDiseaseButton;

    private User currentUser;
    private AppController application;
    private UserController parent;

    /**
     * Gives the user view the application controller and hides all label and buttons that are not
     * needed on opening
     *
     * @param controller the application controller
     * @param user       the current user
     * @param parent     the UserController class this belongs to
     */
    public void init(AppController controller, User user, boolean fromClinician,
                     UserController parent) {
        application = controller;
        currentUser = user;
        this.parent = parent;
        if (fromClinician) {
            addDiseaseButton.setVisible(true);
            updateDiseaseButton.setVisible(true);
            deleteDiseaseButton.setVisible(true);
        }

        setSelectionModels();
        showUserDiseases(currentUser, true);
    }

    private void setSelectionModels() {
        currentDiseaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pastDiseaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        currentDiseaseTableView.getSelectionModel().selectedItemProperty()
                .addListener(listChangeListener -> pastDiseaseTableView.getSelectionModel().select(null));
        pastDiseaseTableView.getSelectionModel().selectedItemProperty().addListener(
                listChangeListener -> currentDiseaseTableView.getSelectionModel().select(null));
    }

    /**
     * show the current and past diseases of the user.
     */
    public void showUserDiseases(User user, boolean init) {
        if (user.getCurrentDiseases().isEmpty()) {
            currentDiseaseTableView.setPlaceholder(new Label("No Current Diseases"));
        }
        ObservableList<Disease> currentDisease = FXCollections
                .observableList(user.getCurrentDiseases().stream().filter(d -> !d.isDeleted()).collect(Collectors.toList()));
        currentDiseaseTableView.setItems(currentDisease);

        if (user.getPastDiseases().isEmpty()) {
            pastDiseaseTableView.setPlaceholder(new Label("No Past Diseases"));
        }
        ObservableList<Disease> pastDisease = FXCollections.observableList(user.getPastDiseases().stream().filter(d -> !d.isDeleted()).collect(Collectors.toList()));
        pastDiseaseTableView.setItems(pastDisease);

        if (init) { //TODO Move this into the actual init function
            TableColumn<Disease, LocalDate> diagnosisDateColumn = new TableColumn<>("Diagnosis Date");
            diagnosisDateColumn.setMinWidth(110);
            diagnosisDateColumn.setMaxWidth(110);
            diagnosisDateColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

            TableColumn<Disease, String> nameColumn = new TableColumn<>("Disease Name");
            nameColumn.setMinWidth(235);
            nameColumn.setMaxWidth(235);
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Disease, Boolean> chronicColumn = new TableColumn<>("Chronic");
            chronicColumn.setMinWidth(70);
            chronicColumn.setMaxWidth(70);
            chronicColumn.setCellValueFactory(new PropertyValueFactory<>("isChronic"));

            chronicColumn.setCellFactory(column -> new TableCell<Disease, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) { //MARKER
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);

                    if (item == null) {
                        return;
                    }

                    if (item) {
                        setText("Chronic");
                        setTextFill(Color.RED);
                    } else {
                        setText("");
                    }
                }
            });
            currentDiseaseTableView.getColumns().addAll(diagnosisDateColumn, nameColumn, chronicColumn);

            TableColumn<Disease, LocalDate> diagnosisDateColumn2 = new TableColumn<>("Diagnosis Date");
            diagnosisDateColumn2.setMinWidth(110);
            diagnosisDateColumn2.setMaxWidth(110);
            diagnosisDateColumn2.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

            TableColumn<Disease, String> nameColumn2 = new TableColumn<>("Disease Name");
            nameColumn2.setMinWidth(305);
            nameColumn2.setMaxWidth(305);
            nameColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));

            pastDiseaseTableView.getColumns().addAll(diagnosisDateColumn2, nameColumn2);
        }
        currentDiseaseTableView.refresh();
        pastDiseaseTableView.refresh();
    }

    /**
     * Checks if a disease is selected in either 'Past' or 'Current' tables. If so, it passes that
     * into NewDiseaseController to open up the 'disease editor' window. NewDiseaseController should
     * probably be renamed to diseaseEditor
     */
    @FXML
    private void updateDisease() {
        currentUser.saveStateForUndo(); //NEW
        if (currentDiseaseTableView.getSelectionModel().getSelectedItem()
                != null) { //Might error, dunno what it returns if nothing is selected, hopefully -1?
            launchDiseasesGui(currentDiseaseTableView.getSelectionModel().getSelectedItem());
        } else if (pastDiseaseTableView.getSelectionModel().getSelectedItem() != null) {
            launchDiseasesGui(pastDiseaseTableView.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Launches a popup gui that updates or adds a new disease
     *
     * @param disease Disease to display on the GUI
     */
    private void launchDiseasesGui(Disease disease) {
        FXMLLoader addDiseaseLoader = new FXMLLoader(
                getClass().getResource("/FXML/createNewDisease.fxml"));
        Parent root;
        try {
            root = addDiseaseLoader.load();
            root.requestFocus(); //Currently the below code thinks that focus = selected so will always take the focused
            // thing in currentDiseases over the selected thing in pastDiseases. Trying to fix
            NewDiseaseController newDiseaseController = addDiseaseLoader.getController();
            Stage stage = new Stage();
            newDiseaseController.init(currentUser, application, stage, disease, parent);
            stage.setScene(new Scene(root));
            stage.show();
            Log.info("successfully launched add/update Diseases pop-up window for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("failed to load add/update Diseases pop-up window for User NHI: " + currentUser.getNhi(), e);
        }


    }


    /**
     * Deletes the currently selected disease by moving it to the past diseases table
     */
    @FXML
    private void deleteDisease() {
        currentUser.saveStateForUndo(); //NEW
        if (currentDiseaseTableView.getSelectionModel().getSelectedIndex() >= 0) {
            if (!currentDiseaseTableView.getSelectionModel().getSelectedItem().getIsChronic()) {
                currentDiseaseTableView.getSelectionModel().getSelectedItem().setDeleted(true);
            } else {
                Log.warning("Unable to delete current disease for User NHI: " + currentUser.getNhi() + ", no disease selected");
                return;
            }
            Log.info("current disease: " + currentDiseaseTableView.getSelectionModel().getSelectedItem() + " deleted for User NHI: " + currentUser.getNhi());
        } else if (pastDiseaseTableView.getSelectionModel().getSelectedIndex() >= 0) {
            pastDiseaseTableView.getSelectionModel().getSelectedItem().setDeleted(true);
            Log.info("past disease: " + pastDiseaseTableView.getSelectionModel().getSelectedItem() + " deleted for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("Unable to delete past disease for User NHI: " + currentUser.getNhi() + ", no disease selected");
        }

        this.application.update(currentUser);
        showUserDiseases(currentUser, false); //Reload the scene?
    }

    /**
     * Refreshes the user's diseases
     *
     * @param isSortedByName  Checks if the table is currently sorted by name
     * @param isReverseSorted checks if the table is sorted in descending order
     */
    public void diseaseRefresh(boolean isSortedByName, boolean isReverseSorted) {
        Disease disease = new Disease("", false, false, LocalDate.now());
        currentUser.getCurrentDiseases().sort(disease.diseaseNameComparator);
        currentUser.getPastDiseases().sort(disease.diseaseDateComparator);

        if (isSortedByName) {
            currentUser.getCurrentDiseases().sort(disease.diseaseNameComparator);
            currentUser.getPastDiseases().sort(disease.diseaseNameComparator);

        }
        if (isReverseSorted) {
            currentUser.getCurrentDiseases().sort(reverseOrder());
            currentUser.getPastDiseases().sort(disease.diseaseNameComparator);
        }
        currentUser.getCurrentDiseases().sort(disease.diseaseChronicComparator);

        showUserDiseases(currentUser, false);
        //currentDiseaseTableView.refresh();
        //pastDiseaseTableView.refresh();

    }

    /**
     * fires when the add button at the Disease tab is clicked
     */
    @FXML
    private void addDisease() {

        FXMLLoader addDiseaseLoader = new FXMLLoader(
                getClass().getResource("/FXML/createNewDisease.fxml"));
        Parent root;
        try {
            root = addDiseaseLoader.load();
            NewDiseaseController newDiseaseController = addDiseaseLoader.getController();
            Stage stage = new Stage();
            Disease disease = new Disease("", false, false, LocalDate.now());
            currentUser.addCurrentDisease(disease);
            newDiseaseController.init(currentUser, application, stage, disease, parent);
            stage.setScene(new Scene(root));
            stage.show();
            Log.info("successfully launched add Diseases pop-up window for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("failed to load add Diseases pop-up window for User NHI: " + currentUser.getNhi(), e);
        }


    }

}
