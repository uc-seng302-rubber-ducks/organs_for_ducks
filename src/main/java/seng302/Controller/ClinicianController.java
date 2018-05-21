package seng302.Controller;

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
import seng302.Model.Clinician;
import seng302.Model.Organs;
import seng302.Model.TransplantDetails;
import seng302.Model.User;
import seng302.Service.AttributeValidation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static seng302.Model.Organs.*;

/**
 * Class for the functionality of the Clinician view of the application
 */
public class ClinicianController {

    private static int searchCount = 0;
    private final int ROWS_PER_PAGE = 30;
    private int startIndex = 0;
    private int endIndex;
    //<editor-fold desc="FXML declarations">
    @FXML
    private Button undoButton;
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
    private TextField waitingRegionTextfield;
    @FXML
    private Tooltip searchToolTip;
    @FXML
    private TableView<User> searchTableView;
    @FXML
    private Pagination searchTablePagination;
    @FXML
    private TableView<TransplantDetails> transplantWaitListTableView;
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
    private Button redoButton;
    @FXML
    private Button logoutButton;
    //</editor-fold>
    private Stage stage;
    private AppController appController;
    private Clinician clinician;
    private List<User> users;
    private ArrayList<Stage> openStages;
    private FilteredList<User> fListUsers;
    private ArrayList<CheckBox> filterCheckBoxList = new ArrayList<>();
    //Initiliase table columns as class level so it is accessible for sorting in pagination methods
    private TableColumn<User, String> lNameColumn;
    private boolean filterVisible = false;


    /**
     * Initializes the controller class for the clinician overview.
     *
     * @param stage         The applications stage.
     * @param appController the applications controller.
     * @param clinician     The current clinician.
     */
    public void init(Stage stage, AppController appController, Clinician clinician) {
        this.stage = stage;
        this.appController = appController;
        this.clinician = clinician.clone();
        stage.setResizable(true);
        showClinician(clinician);
        users = appController.getUsers();
        searchCount = users.size();
        initSearchTable();
        groupCheckBoxes();
        initWaitListTable();

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

        List<User> usersSublist = getSearchData(users);
        //set up lists
        //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
        ObservableList<User> oListUsers = FXCollections.observableList(users);

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

        fListUsers = new FilteredList<>(oListUsers);
        fListUsers = filter(fListUsers);
        FilteredList<User> squished = new FilteredList<>(fListUsers);

        SortedList<User> sListUsers = new SortedList<>(squished);
        sListUsers.comparatorProperty().bind(searchTableView.comparatorProperty());

        //predicate on this list not working properly
        //should limit the number of items shown to ROWS_PER_PAGE
        //squished = limit(fListUsers, sListUsers);
        //set table columns and contents
        searchTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);
        //searchTableView.setItems(FXCollections.observableList(sListUsers.subList(startIndex, endIndex)));
        searchTableView.setItems(sListUsers);
        searchTableView.setRowFactory((searchTableView) -> new TooltipTableRow<>(User::getTooltip));


        //set on-click behaviour
        searchTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                User user = searchTableView.getSelectionModel().getSelectedItem();
                launchUser(user);
            }
        });
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
        for (User user : users) {
            if (user.isReceiver() && (user.getDeceased() != null || !user.getDeceased())) {
                Set<Organs> organs = user.getReceiverDetails().getOrgans().keySet();
                for (Organs organ : organs) {
                    if (isReceiverNeedingFilteredOrgan(user.getNhi(), organs).contains(organ)) {
                        appController.addTransplant(
                                new TransplantDetails(user.getNhi(), user.getFullName(), organ, LocalDate.now(),
                                        user.getRegion()));
                    }
                }
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
                    User wantedUser = appController.findUser(transplantDetails.getNhi());
                    launchUser(wantedUser);
                }
            });

        } else {
            transplantWaitListTableView.setOnMouseClicked(null);
            transplantWaitListTableView.setItems(null);
            transplantWaitListTableView.setPlaceholder(new Label("No Receivers currently registered"));
        }
    }

    /**
     * @param arrayList An array list of users.
     * @return A list of users.
     */
    private List<User> getSearchData(List<User> arrayList) {
        return arrayList.subList(startIndex, endIndex);
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
    private void launchUser(User user) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage userStage = new Stage();
            userStage.setScene(new Scene(root));
            openStages.add(userStage);
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            userController.init(AppController.getInstance(), user, userStage, true);
            userStage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

            //System.out.println(user);
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

    /**
     * Undoes the last action and redisplays the clinician.
     */
    @FXML
    private void undo() {
        clinician.undo();
        undoButton.setDisable(clinician.getUndoStack().empty());
        showClinician(clinician);
    }

    /**
     * Redoes the last action and redisplays the clinician.
     */
    @FXML
    public void redo() {
        clinician.redo();
        redoButton.setDisable(clinician.getRedoStack().empty());
        showClinician(clinician);
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
        } catch (IOException e) {
            e.printStackTrace();
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

        } catch (IOException e) {
            e.printStackTrace();
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

    public void refreshTables() {
        populateWaitListTable();
        searchTableView.refresh();
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
            deletedUserController.init();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disableLogout() {
        logoutButton.setVisible(false);
    }
}
