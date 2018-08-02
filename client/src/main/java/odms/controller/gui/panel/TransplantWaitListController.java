package odms.controller.gui.panel;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import odms.commons.model.User;
import odms.commons.model._abstract.TransplantWaitListViewer;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static odms.commons.model._enum.Organs.*;

public class TransplantWaitListController {
    @FXML
    private CheckBox boneCheckBox;

    @FXML
    private CheckBox boneMarrowCheckBox;

    @FXML
    private CheckBox corneaCheckBox;

    @FXML
    private CheckBox connectiveTissueCheckBox;

    @FXML
    private CheckBox heartCheckBox;

    @FXML
    private CheckBox middleEarCheckBox;

    @FXML
    private CheckBox intestineCheckBox;

    @FXML
    private CheckBox pancreasCheckBox;

    @FXML
    private CheckBox lungCheckBox;

    @FXML
    private CheckBox kidneyCheckBox;

    @FXML
    private CheckBox liverCheckBox;

    @FXML
    private CheckBox skinCheckBox;

    @FXML
    private Label filtersLabel;

    @FXML
    private TextField waitingRegionTextfield;

    @FXML
    private Button transplantNextPageButton;

    @FXML
    private Button transplantPrevPageButton;

    @FXML
    private TableView<TransplantDetails> transplantWaitListTableView;

    private ArrayList<CheckBox> filterCheckBoxList = new ArrayList<>();
    private List<User> users;
    private AppController appController;
    private TransplantWaitListViewer parent;
    private int startIndex = 0;
    private static final int TRANSPLANTS_PER_PAGE = 30;

    //for delaying the request when typing into the region text field
    private PauseTransition pause = new PauseTransition(Duration.millis(300));

    @FXML
    public void init(AppController controller, TransplantWaitListViewer parent) {
        this.parent = parent;
        appController = controller;
        users = controller.getUsers();
        groupCheckBoxes();
        initWaitListTable();
    }

    /**
     * Adds the filtering checkboxes to a list to later iterate through them
     */
    private void groupCheckBoxes() {
        filterCheckBoxList.add(boneCheckBox);
        filterCheckBoxList.add(boneMarrowCheckBox);
        filterCheckBoxList.add(corneaCheckBox);
        filterCheckBoxList.add(connectiveTissueCheckBox);
        filterCheckBoxList.add(heartCheckBox);
        filterCheckBoxList.add(kidneyCheckBox);
        filterCheckBoxList.add(lungCheckBox);
        filterCheckBoxList.add(liverCheckBox);
        filterCheckBoxList.add(middleEarCheckBox);
        filterCheckBoxList.add(intestineCheckBox);
        filterCheckBoxList.add(skinCheckBox);
        filterCheckBoxList.add(pancreasCheckBox);
    }

    /**
     * initialises the Wait List table, abstracted from main init function for clarity
     */
    private void initWaitListTable() {

        TableColumn<TransplantDetails, String> recipientNameColumn = new TableColumn<>("Name");
        recipientNameColumn.setMinWidth(220);
        recipientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<TransplantDetails, String> organNameColumn = new TableColumn<>("Organ");
        organNameColumn.setMinWidth(150);
        organNameColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));

        TableColumn<TransplantDetails, Integer> organRegistrationDateColumn = new TableColumn<>("ORD");
        organRegistrationDateColumn.setMinWidth(100);
        organRegistrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("oRD"));

        TableColumn<TransplantDetails, String> recipientRegionColumn = new TableColumn<>("Region");
        recipientRegionColumn.setMinWidth(140);
        recipientRegionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        transplantWaitListTableView.getColumns().setAll(recipientNameColumn, organNameColumn, organRegistrationDateColumn, recipientRegionColumn);

        populateWaitListTable();
        setInputOnClickBehaviour();
    }

    /**
     * simplified version of the method to get the first page of results with no filters
     */
    private void populateWaitListTable() {
        populateWaitListTable(0, TRANSPLANTS_PER_PAGE, waitingRegionTextfield.getText(), getAllFilteredOrgans());
    }


    /**
     * populates the wait list table from the server based on the following filter information
     * @param startIndex first result to return
     * @param count number of results to return
     * @param regionSearch partial string to filter region by (sql LIKE 'string%')
     * @param allowedOrgans list of the organs to filter by. If this is empty, all will be returned
     */
    private void populateWaitListTable(int startIndex, int count, String regionSearch, List<Organs> allowedOrgans) {
        appController.getTransplantList().clear();
        appController.getTransplantBridge().getWaitingList(startIndex, count, "", regionSearch, allowedOrgans);
        displayWaitListTable();
    }

    public void displayWaitListTable() {
        ObservableList<TransplantDetails> oTransplantList = FXCollections
                .observableList(appController.getTransplantList());
        SortedList<TransplantDetails> sTransplantList = new SortedList<>(oTransplantList);
        sTransplantList.comparatorProperty().bind(transplantWaitListTableView.comparatorProperty());

        if (!appController.getTransplantList().isEmpty()) {
            transplantWaitListTableView.setItems(sTransplantList);

        } else {
            transplantWaitListTableView.setItems(null);
            Platform.runLater(() -> transplantWaitListTableView.setPlaceholder(new Label("No Receivers currently registered"))); // Do this to prevent threading issues when this method is not called on an FX thread;
        }

        //needs to be re-run each time as a handler on empty list will cause issues
        setTableOnClickBehaviour();
    }


    /**
     * sets table so that clicking an entry will take open that user in a new window
     */
    private void setTableOnClickBehaviour() {
        if (!appController.getTransplantList().isEmpty()) {
            transplantWaitListTableView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    TransplantDetails transplantDetails = transplantWaitListTableView.getSelectionModel().getSelectedItem();
                    try {
                        User wantedUser = appController.getUserBridge().getUser(transplantDetails.getNhi());
                        parent.launchUser(UserOverview.fromUser(wantedUser)); //TODO: This is a bit disgusting fix
                    } catch (IOException e) {
                        AlertWindowFactory.generateError(e);
                    }
                }
            });
        } else {
            transplantWaitListTableView.setOnMouseClicked(null);
        }
    }

    /**
     * setup all inputs to re-send a request when they are changed
     */
    private void setInputOnClickBehaviour() {
        for(CheckBox checkBox : filterCheckBoxList) {
            //get the first page of results for the new filter settings
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> populateWaitListTable());
        }
        waitingRegionTextfield.textProperty().addListener(observable -> {
            pause.setOnFinished(e -> populateWaitListTable());
            pause.playFromStart();
        });

    }

    /**
     * handler to move to the next page of the transplant list.
     * checks if there is another page to go to and makes the request for new data
     */
    @FXML
    public void getNextPage() {
        //this is the last page
        if (appController.getTransplantList().size() < TRANSPLANTS_PER_PAGE) {
            return;
        }

        startIndex += TRANSPLANTS_PER_PAGE;
        List<Organs> organs = new ArrayList<>();
        if (checkAnyTransplantFilterCheckBoxSelected()) {
            organs = getAllFilteredOrgans();
        }
        populateWaitListTable(startIndex, TRANSPLANTS_PER_PAGE, waitingRegionTextfield.getText(), organs);
    }

    /**
     * handler to move to the previous page of the transplant list.
     * checks if there is prior page to go to and makes the request for new data
     */
    @FXML
    public void getPrevPage() {
        //this is the first page
        if (startIndex - TRANSPLANTS_PER_PAGE < 0) {
            return;
        }
        startIndex -= TRANSPLANTS_PER_PAGE;
        List<Organs> organs = new ArrayList<>();
        if (checkAnyTransplantFilterCheckBoxSelected()) {
            organs = getAllFilteredOrgans();
        }
        populateWaitListTable(startIndex, TRANSPLANTS_PER_PAGE, waitingRegionTextfield.getText(), organs);

    }

    /**
     * @return true if any of the transplant filter check boxes are checked
     */
    private boolean checkAnyTransplantFilterCheckBoxSelected() {
        for (CheckBox checkBox : filterCheckBoxList) {
            if (checkBox.isSelected()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a list of organs that the user wants to filter by
     *
     * @return the relevant organs to filter by
     */
    private List<Organs> getAllFilteredOrgans() {
        List<Organs> organs = new ArrayList<>();
        if (boneCheckBox.isSelected())
            organs.add(BONE);
        if (boneMarrowCheckBox.isSelected())
            organs.add(BONE_MARROW);
        if (corneaCheckBox.isSelected())
            organs.add(CORNEA);
        if (connectiveTissueCheckBox.isSelected())
            organs.add(CONNECTIVE_TISSUE);
        if (heartCheckBox.isSelected())
            organs.add(HEART);
        if (middleEarCheckBox.isSelected())
            organs.add(MIDDLE_EAR);
        if (intestineCheckBox.isSelected())
            organs.add(INTESTINE);
        if (pancreasCheckBox.isSelected())
            organs.add(PANCREAS);
        if (lungCheckBox.isSelected())
            organs.add(LUNG);
        if (kidneyCheckBox.isSelected())
            organs.add(KIDNEY);
        if (liverCheckBox.isSelected())
            organs.add(LIVER);
        if (skinCheckBox.isSelected())
            organs.add(SKIN);
        return organs;
    }


}
