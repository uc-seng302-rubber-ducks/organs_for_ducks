package seng302.controller.gui.window;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.controller.AppController;
import seng302.controller.gui.panel.TransplantWaitListController;
import seng302.controller.gui.popup.DeletedUserController;
import seng302.controller.gui.statusBarController;
import seng302.model.Clinician;
import seng302.model.TooltipTableRow;
import seng302.model.User;
import seng302.model._abstract.TransplantWaitListViewer;
import seng302.model._enum.EventTypes;
import seng302.model._enum.Organs;
import seng302.utils.AttributeValidation;
import seng302.utils.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;

/**
 * Class for the functionality of the Clinician view of the application
 */
public class ClinicianController implements PropertyChangeListener, TransplantWaitListViewer {

    private static final int ROWS_PER_PAGE = 30;
    private int startIndex = 0;
    private int endIndex;
    //<editor-fold desc="FXML declarations">
    @FXML
    private Button undoButton;

    @FXML
    private Button backButton;

    @FXML
    private Label staffIdLabel;
    @FXML
    private Label fNameLabel;
    @FXML
    private Label mNameLabel;
    @FXML
    private Label lNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private TextField searchTextField;


    @FXML
    private Tooltip searchToolTip;
    @FXML
    private TableView<User> searchTableView;
    @FXML
    private Pagination searchTablePagination;


    @FXML
    private Label searchCountLabel;
    @FXML
    private AnchorPane filterAnchorPane;
    @FXML
    private ComboBox genderComboBox;
    @FXML
    private TextField regionSearchTextField;
    @FXML
    private CheckBox donorFilterCheckBox;
    @FXML
    private CheckBox receiverFilterCheckBox;
    @FXML
    private CheckBox allCheckBox;
    @FXML
    private Button expandButton;
    @FXML
    private TransplantWaitListController transplantWaitListTabPageController;
    @FXML
    private Button redoButton;
    @FXML
    private Button logoutButton;

    @FXML
    private Button deleteClinicianButton;

    @FXML
    private statusBarController statusBarPageController;

    //</editor-fold>

    private Stage stage;
    private AppController appController;
    private Clinician clinician;
    private List<User> users;
    private ArrayList<Stage> openStages;
    private FilteredList<User> fListUsers;


    //Initiliase table columns as class level so it is accessible for sorting in pagination methods
    private TableColumn<User, String> lNameColumn;
    private boolean filterVisible = false;
    private int searchCount;

    private Collection<PropertyChangeListener> parentListeners;

    private boolean admin = false;

    /**
     * Initializes the controller class for the clinician overview.
     *
     * @param stage         The applications stage.
     * @param appController the applications controller.
     * @param clinician     The current clinician.
     */
    public void init(Stage stage, AppController appController, Clinician clinician, boolean fromAdmin,
                     Collection<PropertyChangeListener> parentListeners) {

        //add change listeners of parent controllers to the current clinician
        this.parentListeners = new ArrayList<>();
        if (parentListeners != null && !parentListeners.isEmpty()) {
            for (PropertyChangeListener listener : parentListeners) {
                clinician.addPropertyChangeListener(listener);
            }
            this.parentListeners.addAll(parentListeners);
        }
        this.stage = stage;
        this.appController = appController;
        this.clinician = clinician.clone();
        this.admin = fromAdmin;
        stage.setResizable(true);
        showClinician(clinician);
        users = appController.getUsers();
        searchCount = users.size();
        initSearchTable();
        transplantWaitListTabPageController.init(appController, this);
        statusBarPageController.init(appController);

        if (clinician.getStaffId().equals("0")) {
            deleteClinicianButton.setDisable(true);
        }

        setDefaultFilters();
        searchCountLabel.setText("Showing results " + (searchCount == 0 ? startIndex : startIndex + 1) + " - " + (endIndex) + " of " + searchCount);
        openStages = new ArrayList<>();
        stage.setOnCloseRequest(e -> {
            if (!openStages.isEmpty()) {
                for (Stage s : openStages) {
                    s.close();
                }
            }
        });
        int pageCount = searchCount / ROWS_PER_PAGE;
        searchTablePagination.setPageCount(pageCount > 0 ? pageCount + 1 : 1);
        searchTablePagination.currentPageIndexProperty().addListener(((observable, oldValue, newValue) -> changePage(newValue.intValue())));

        if (fromAdmin) {
            logoutButton.setVisible(false);
            backButton.setVisible(true);
        } else {
            logoutButton.setVisible(true);
            backButton.setVisible(false);
        }

    }

    @FXML
    private void goBack() {
        appController.updateClinicians(clinician);
        stage.close();
        Log.info("Successfully closed update user window for Clinician StaffID: " + clinician.getStaffId());
    }

    private void setDefaultFilters() {
        allCheckBox.setSelected(true);
    }

    /**
     * initialises the clinicians details
     */
    public void showClinician(Clinician clinician) {
        this.clinician = clinician;
        staffIdLabel.setText(clinician.getStaffId());
        fNameLabel.setText(clinician.getFirstName());
        mNameLabel.setText(clinician.getMiddleName());
        lNameLabel.setText(clinician.getLastName());
        addressLabel.setText(clinician.getWorkAddress());
        regionLabel.setText(clinician.getRegion());
        if (clinician.getFirstName() == null) {
            stage.setTitle("Clinician: Admin");
        } else if (clinician.getLastName() == null) {
            stage.setTitle("Clinician: " + clinician.getFirstName());
        } else {
            stage.setTitle("Clinician: " + clinician.getFirstName() + " " + clinician.getLastName());
        }
        undoButton.setDisable(clinician.getUndoStack().empty());
        redoButton.setDisable(clinician.getRedoStack().empty());
        if (clinician.getChanges().size() > 0) {
            statusBarPageController.updateStatus(clinician.getStaffId() + " " + clinician.getChanges().get(clinician.getChanges().size() - 1).getChange());

        }
    }

    /**
     * initialises the search table, abstracted from main init function for clarity
     */
    private void initSearchTable() {
        TableColumn<User, String> fNameColumn;
        TableColumn<User, String> dobColumn;
        TableColumn<User, String> dodColumn;
        TableColumn<User, String> ageColumn;
        TableColumn<User, HashSet<Organs>> organsColumn;
        TableColumn<User, String> regionColumn;

        endIndex = Math.min(startIndex + ROWS_PER_PAGE, users.size());
        if (users.isEmpty()) {
            return;
        }

        fNameColumn = new TableColumn<>("First name");
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        lNameColumn = new TableColumn<>("Last name");
        lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        dodColumn = new TableColumn<>("Date of Death");
        dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));

        ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        regionColumn = new TableColumn<>("Region");
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        organsColumn = new TableColumn<>("Organs");
        organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

        //add more columns as wanted/needed

        //predicate on this list not working properly
        //should limit the number of items shown to ROWS_PER_PAGE
        //squished = limit(fListUsers, sListUsers);
        //set table columns and contents
        searchTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);
        //searchTableView.setItems(FXCollections.observableList(sListUsers.subList(startIndex, endIndex)));

        displaySearchTable();
        //set on-click behaviour
        searchTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                User user = searchTableView.getSelectionModel().getSelectedItem();
                launchUser(user);
            }
        });
    }

    private void displaySearchTable() {
        //set up lists
        //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
        ObservableList<User> oListUsers = FXCollections.observableList(users);

        fListUsers = new FilteredList<>(oListUsers);
        fListUsers = filter(fListUsers);
        FilteredList<User> squished = new FilteredList<>(fListUsers);

        SortedList<User> sListUsers = new SortedList<>(squished.filtered(user -> !user.isDeleted()));
        sListUsers.comparatorProperty().bind(searchTableView.comparatorProperty());

        searchTableView.setItems(sListUsers);
        searchTableView.setRowFactory((searchTableView) -> new TooltipTableRow<>(User::getTooltip));
    }


    /**
     * @param pageIndex the current page.
     * @return the search table view node.
     */
    private Node changePage(int pageIndex) {
        startIndex = pageIndex * ROWS_PER_PAGE;
        endIndex = Math.min(startIndex + ROWS_PER_PAGE, users.size());

        int minIndex = Math.min(endIndex, fListUsers.size());

        SortedList<User> sListUsers = new SortedList<>(FXCollections.observableArrayList(
                fListUsers.subList(Math.min(startIndex, minIndex), minIndex)));
        sListUsers.comparatorProperty().bind(searchTableView.comparatorProperty());

        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        searchTableView.setItems(sListUsers);


        int pageCount = searchCount / ROWS_PER_PAGE;
        searchTablePagination.setPageCount(pageCount > 0 ? pageCount + 1 : 1);
        searchCountLabel.setText("Showing results " + (searchCount == 0 ? startIndex : startIndex + 1) + " - " + (minIndex) + " of " + searchCount);

        return searchTableView;
    }

    /**
     * @param user the selected user.
     */
    public void launchUser(User user) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage userStage = new Stage();
            userStage.setScene(new Scene(root));
            openStages.add(userStage);
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            //Ostrich
            parentListeners.add(this);
            userController.init(AppController.getInstance(), user, userStage, true, parentListeners);
            userStage.show();
            Log.info("Clinician " + clinician.getStaffId()
                    + " successfully launched user overview window");

            ArrayList<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            userController.init(AppController.getInstance(), user, userStage, true, listeners);
            userStage.show();
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " Failed to load user overview window", e);
        }
    }

    /**
     * applies a change listener to the input text box and filters a filtered list accordingly
     *
     * @param fListUsers list to be filtered
     * @return filtered list with filter applied
     */
    private FilteredList<User> filter(FilteredList<User> fListUsers) {
        setTextFieldListener(searchTextField, fListUsers);
        setTextFieldListener(regionSearchTextField, fListUsers);
        setCheckBoxListener(donorFilterCheckBox, fListUsers);
        setCheckBoxListener(receiverFilterCheckBox, fListUsers);
        setCheckBoxListener(allCheckBox, fListUsers);
        genderComboBox.valueProperty()
                .addListener((observable -> setFilteredListPredicate(fListUsers)));

        searchTablePagination.setPageCount(searchCount / ROWS_PER_PAGE);
        return fListUsers;
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param inputTextField textfield to add the listener to
     * @param fListUsers     filteredList object of users to set predicate property of
     */
    private void setTextFieldListener(TextField inputTextField, FilteredList<User> fListUsers) {
        inputTextField.textProperty()
                .addListener((observable) -> setFilteredListPredicate(fListUsers));
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param checkBox   checkBox object to add the listener to
     * @param fListUsers filteredList object of users to set predicate property of
     */
    private void setCheckBoxListener(CheckBox checkBox, FilteredList<User> fListUsers) {
        checkBox.selectedProperty()
                .addListener(((observable) -> setFilteredListPredicate(fListUsers)));
    }

    /**
     * Sets the predicate property of filteredList to filter by specific properties
     *
     * @param fList filteredList object to modify the predicate property of
     */
    private void setFilteredListPredicate(FilteredList<User> fList) {
        searchCount = 0; //refresh the searchCount every time so it recalculates it each search
        fList.predicateProperty().bind(Bindings.createObjectBinding(() -> user -> {
            String lowerCaseFilterText = searchTextField.getText().toLowerCase();
            boolean regionMatch = AttributeValidation.checkRegionMatches(regionSearchTextField.getText(), user);
            boolean genderMatch = AttributeValidation.checkGenderMatches(genderComboBox.getValue().toString(), user);

            if (AttributeValidation.checkTextMatches(lowerCaseFilterText, user.getFirstName()) ||
                    AttributeValidation.checkTextMatches(lowerCaseFilterText, user.getLastName()) &&
                            (regionMatch) && (genderMatch) &&
                            (((user.isDonor() == donorFilterCheckBox.isSelected()) &&
                                    (user.isReceiver() == receiverFilterCheckBox.isSelected())) || allCheckBox.isSelected())) {
                searchCount++;
                return true;
            }

            //if (other test case) return true
            return false;
        }));
        changePage(searchTablePagination.getCurrentPageIndex());
    }

    /**
     * Undoes the last action and redisplays the clinician.
     */
    @FXML
    private void undo() {
        clinician.undo();
        undoButton.setDisable(clinician.getUndoStack().empty());
        showClinician(clinician);
        Log.info("Clinician " + clinician.getStaffId() + " executed undo clinician");
    }

    /**
     * Redoes the last action and redisplays the clinician.
     */
    @FXML
    public void redo() {
        clinician.redo();
        redoButton.setDisable(clinician.getRedoStack().empty());
        showClinician(clinician);
        Log.info("Clinician " + clinician.getStaffId() + " executed redo clinician");
    }

    /**
     * Returns the user to the login screen
     */
    @FXML
    void logout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loader.load();
            stage.setScene(new Scene(root));
            LoginController loginController = loader.getController();
            loginController.init(AppController.getInstance(), stage);
            stage.hide();
            stage.show();
            stage.hide();
            stage.show();
            Log.info("Clinician " + clinician.getStaffId() + " successfully launched login window after logout");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " failed to launch login window after logout", e);
        }
    }

    /**
     * Opens an edit window for the clinicians personal details
     */
    @FXML
    void edit() {
        FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
        Parent root;
        try {
            root = updateLoader.load();
            UpdateClinicianController updateClinicianController = updateLoader.getController();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            updateClinicianController.init(clinician, appController, stage, false, newStage);
            newStage.initModality(Modality.APPLICATION_MODAL); // background window is no longer selectable
            newStage.showAndWait();
            showClinician(clinician);
            Log.info("Clinician " + clinician.getStaffId() + " successfully launched update clinician window");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " failed to launch update clinician window", e);
        }
    }

    /**
     * Callback method to change the divider position to show advanced filtering options in the GUI
     */
    @FXML
    private void expandFilter() {
        double dividerPos = filterVisible ? 44 : 150;
        filterAnchorPane.setMinHeight(dividerPos);
        filterAnchorPane.setMaxHeight(dividerPos);
        filterVisible = !filterVisible;
        expandButton.setText(filterVisible ? "▲" : "▼");
    }

    /**
     * Callback method to refresh the tables in the view
     */
    @FXML
    public void refreshTables() {
        transplantWaitListTabPageController.populateWaitListTable();
        displaySearchTable();
    }

    /**
     * Loads the recently deleted users window
     */
    @FXML
    public void loadRecentlyDeleted() {
        FXMLLoader deletedUserLoader = new FXMLLoader(
                getClass().getResource("/FXML/deletedUsersView.fxml"));
        Parent root;
        try {
            root = deletedUserLoader.load();
            DeletedUserController deletedUserController = deletedUserLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            deletedUserController.init(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            Log.info("Clinician " + clinician.getStaffId() + " successfully launched delete user window");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " failed to launch delete user window", e);
        }
    }

    /**
     * Deletes the clinician profile after confirmation.
     */
    @FXML
    private void deleteClinician() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this clinician?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            clinician.setDeleted(true);
            if (!admin) {
                appController.deleteClinician(clinician);
                logout();
            } else {
                stage.close();
            }
        }
    }

    public void disableLogout() {
        logoutButton.setVisible(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        //clinician controller watches user model
        //refresh view/tables etc. on change
        if (evt.getPropertyName().equals(EventTypes.USER_UPDATE.name())) {
            refreshTables();
        }
    }
}
