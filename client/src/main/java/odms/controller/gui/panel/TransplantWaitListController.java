package odms.controller.gui.panel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import odms.commons.exception.ApiException;
import odms.commons.model.User;
import odms.commons.model._abstract.TransplantWaitListViewer;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.utils.TransplantBridge;
import odms.utils.UserBridge;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private TableView<TransplantDetails> transplantWaitListTableView;

    private ArrayList<CheckBox> filterCheckBoxList = new ArrayList<>();
    private List<User> users;
    private AppController appController;
    private TransplantWaitListViewer parent;
    private int startIndex = 0;
    private static final int TRANSPLANTS_PER_PAGE = 10;

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
        updateFiltersLabel();
        populateWaitListTable();
    }

    /**
     * populates and add double click functionality to the Wait List Table.
     */
    public void populateWaitListTable() {
        //transplantWaitListTableView.getItems().clear();
        //set up lists
        //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
        appController.getTransplantList().clear();
        List<TransplantDetails> details = null;
        try {
            details = appController.getTransplantBridge().getWaitingList(0, 10, "", "", new ArrayList<>());
        } catch (ApiException ex) {
            Log.warning("failed to get transplant details from server. got response code " + ex.getResponseCode(), ex);
        }
        if (details != null) {
            for(TransplantDetails detail : details) {
                appController.addTransplant(detail);
            }
        }

        ObservableList<TransplantDetails> observableTransplantList = FXCollections
                .observableList(appController.getTransplantList());
        FilteredList<TransplantDetails> fTransplantList = new FilteredList<>(
                observableTransplantList);
        fTransplantList = filterTransplantDetails(fTransplantList);
        SortedList<TransplantDetails> sTransplantList = new SortedList<>(fTransplantList);
        sTransplantList.comparatorProperty().bind(transplantWaitListTableView.comparatorProperty());

        if (appController.getTransplantList().size() != 0) {
            transplantWaitListTableView.setItems(sTransplantList);

            //set on-click behaviour
            transplantWaitListTableView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    TransplantDetails transplantDetails = transplantWaitListTableView.getSelectionModel().getSelectedItem();
                    try {
                        User wantedUser = new UserBridge(new OkHttpClient()).getUser(transplantDetails.getNhi());
                        parent.launchUser(UserOverview.fromUser(wantedUser)); //TODO: This is a bit disgusting fix
                    } catch (IOException e) {
                        AlertWindowFactory.generateError(e);
                    }
                }
            });

        } else {
            transplantWaitListTableView.setOnMouseClicked(null);
            transplantWaitListTableView.setItems(null);
            transplantWaitListTableView.setPlaceholder(new Label("No Receivers currently registered"));
        }
    }

    /**
     * Sets listeners for the transplant waiting list
     *
     * @param fListTransplantDetails filtered list to add listener to.
     * @return the filtered list.
     */
    private FilteredList<TransplantDetails> filterTransplantDetails(FilteredList<TransplantDetails> fListTransplantDetails) {
        waitingRegionTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            setTransplantListPredicate(fListTransplantDetails);
            updateFiltersLabel();
            transplantWaitListTableView.refresh();
        });
        for (CheckBox checkBox : filterCheckBoxList) {
            checkBox.selectedProperty().addListener((observable -> {
                setTransplantListPredicate(fListTransplantDetails);
                updateFiltersLabel();
                transplantWaitListTableView.refresh();
            }));
        }

        return fListTransplantDetails;
    }

    /**
     * Updates the filters label to allow the user to easily see the filters that have been applied
     */
    private void updateFiltersLabel() {
        StringBuilder labelText = new StringBuilder("Showing all receivers");
        if (!waitingRegionTextfield.getText().isEmpty()) {
            labelText.append(" from a place starting with ")
                    .append(waitingRegionTextfield.getText());
        }
        if (!getAllFilteredOrgans().isEmpty()) {
            labelText.append(" who need a ");
        }
        for (Organs organ : getAllFilteredOrgans()) {
            labelText.append(organ.toString()).append(" or ");
        }
        if (!getAllFilteredOrgans().isEmpty()) {
            labelText = new StringBuilder(labelText.substring(0, labelText.length() - 4));
        }

        filtersLabel.setText(labelText.toString());


    }

    /**
     * Sets the predicate on the filteredList for Transplant details
     * Cannot be overloaded as the the argument types are the same but have different erasures
     *
     * @param fList filteredList of TransplantDetails objects
     */
    private void setTransplantListPredicate(FilteredList<TransplantDetails> fList) {
        fList.predicateProperty().bind(Bindings.createObjectBinding(() -> transplantDetails ->
                (AttributeValidation.checkRegionMatches(waitingRegionTextfield.getText(), transplantDetails)
                        &&
                        (isReceiverNeedingFilteredOrgan(transplantDetails.getNhi(),
                                new HashSet<>(getAllFilteredOrgans())).contains(transplantDetails.getOrgan())
                                || !checkAnyTransplantFilterCheckBoxSelected()))
        ));
    }

    /**
     * Takes an user with an existing nhi and the set of organs
     *
     * @param nhi    NHI of a user that exists within the system
     * @param organs A set of organs for which the checkboxes are ticked
     * @return An arraylist of Organs enums which the user with the NHI is receiving
     */
    private ArrayList<Organs> isReceiverNeedingFilteredOrgan(String nhi,
                                                             Set<Organs> organs) {
        ArrayList<Organs> result = new ArrayList<>();
        for (Organs o : organs) {
            if (appController.findUser(nhi).getReceiverDetails()
                    .isCurrentlyWaitingFor(o)) {
                result.add(o);
            }
        }
        return result;
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
    private ArrayList<Organs> getAllFilteredOrgans() {
        ArrayList<Organs> organs = new ArrayList<>();
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
