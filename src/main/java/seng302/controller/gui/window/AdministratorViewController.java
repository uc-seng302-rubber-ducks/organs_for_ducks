package seng302.controller.gui.window;

import com.sun.javafx.stage.StageHelper;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.controller.*;
import seng302.controller.gui.FileSelectorController;
import seng302.controller.gui.panel.TransplantWaitListController;
import seng302.controller.gui.popup.AlertUnclosedWindowsController;
import seng302.controller.gui.popup.DeletedUserController;
import seng302.controller.gui.statusBarController;
import seng302.model.*;
import seng302.model._abstract.TransplantWaitListViewer;
import seng302.model._enum.EventTypes;
import seng302.model._enum.Organs;
import seng302.utils.AttributeValidation;
import seng302.utils.JsonHandler;
import seng302.utils.Log;
import seng302.view.CLI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AdministratorViewController implements PropertyChangeListener, TransplantWaitListViewer {

    //<editor-fold desc="FXML stuff">

    private static int searchCount = 0;
    private final int ROWS_PER_PAGE = 30;
    @FXML
    private TableView<User> userTableView;
    @FXML
    private Label adminLastNameLabel;
    @FXML
    private CheckBox allCheckBox;
    @FXML
    private TableView<Clinician> clinicianTableView;
    @FXML
    private TextField cliInputTextField;
    @FXML
    private TableView<Administrator> adminTableView;
    @FXML
    private TextArea adminCliTextArea;
    @FXML
    private AnchorPane filterAnchorPane;
    @FXML
    private Button adminRedoButton;
    @FXML
    private Label adminUsernameLable;
    @FXML
    private Button expandButton;
    @FXML
    private TextField adminSearchField;
    @FXML
    private Pagination searchTablePagination;
    @FXML
    private Button deleteAdminButton;
    @FXML
    private ComboBox<?> genderComboBox;
    @FXML
    private Label adminFirstnameLabel;
    @FXML
    private CheckBox donorFilterCheckBox;
    @FXML
    private Button adminUndoButton;
    @FXML
    private Label searchCountLabel;
    @FXML
    private Label adminMiddleNameLabel;
    @FXML
    private CheckBox receiverFilterCheckBox;
    @FXML
    private Label donorStatusLabel;
    @FXML
    private Label birthGenderLabel;
    @FXML
    private TextField regionSearchTextField;
    @FXML
    private Label fileNotFoundLabel;
    @FXML
    private RadioButton adminUserRadioButton;
    @FXML
    private RadioButton adminClinicianRadioButton;
    @FXML
    private RadioButton adminAdminRadioButton;
    @FXML
    private ToggleGroup adminSearchRadios;

    //</editor-fold>
    @FXML
    private TransplantWaitListController transplantWaitListTabPageController;
    @FXML
    private statusBarController statusBarPageController;
    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -2;
    private boolean owner;
    private int startIndex = 0;
    private int endIndex;
    private FilteredList<User> fListUsers;
    private FilteredList<Clinician> fListClinicians;
    private FilteredList<Administrator> fListAdmins;
    private TableColumn<User, String> lNameColumn;
    private boolean filterVisible = false;
    private TableView<?> activeTableView;

    /**
     * Initialises scene for the administrator view
     *
     * @param administrator administrator to view
     * @param appController appController instance to get data from
     * @param stage         stage to display on
     */
    public void init(Administrator administrator, AppController appController, Stage stage, boolean owner, Collection<PropertyChangeListener> parentListeners) {
        this.stage = stage;
        this.appController = appController;
        this.administrator = administrator;
        this.owner = owner;
        statusBarPageController.init(appController);
        displayDetails();
        transplantWaitListTabPageController.init(appController, this);

        //add change listeners of parent controllers to the current user
        if (parentListeners != null && !parentListeners.isEmpty()) {
            for (PropertyChangeListener listener : parentListeners) {
                administrator.addPropertyChangeListener(listener);
            }
        }
        adminUndoButton.setDisable(true);
        adminRedoButton.setDisable(true);
        if (administrator.getUserName().equals("default")) {
            deleteAdminButton.setDisable(true);
        }

        adminCliTextArea.setEditable(false);
        adminCliTextArea.setFocusTraversable(false);
        cliInputTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendInputToCLI();
                cliInputTextField.setText("");
            } else if (e.getCode() == KeyCode.UP) {
                if (pastCommandIndex >= 0) {
                    pastCommandIndex = pastCommandIndex == 0 ? 0
                            : pastCommandIndex - 1; // makes sure pastCommandIndex is never < 0
                    cliInputTextField.setText(pastCommands.get(pastCommandIndex));
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                if (pastCommandIndex < pastCommands.size() - 1) {
                    pastCommandIndex++;
                    cliInputTextField.setText(pastCommands.get(pastCommandIndex));
                } else if (pastCommandIndex == pastCommands.size() - 1) {
                    pastCommandIndex++;
                    cliInputTextField.setText("");
                }
            }
        });

        addListeners();
        initClinicianSearchTable();
        initAdminSearchTable();
        initUserSearchTable();
        clinicianTableView.setVisible(false);
        adminTableView.setVisible(false);
    }

    /**
     * Sends the input to CLI and redirects the output stream to a new ByteArrayOutputStream and sends
     * the results to the textArea
     */
    private void sendInputToCLI() {
        PrintStream stdOut = System.out;
        PrintStream stdErr = System.err;
        ByteArrayOutputStream areaOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(areaOut));
        System.setErr(new PrintStream(areaOut));
        pastCommands.add(cliInputTextField.getText());
        pastCommandIndex = pastCommands.size();
        CLI.parseInput(cliInputTextField.getText(), appController);
        adminCliTextArea.appendText("\n" + areaOut.toString());
        System.setOut(stdOut);
        System.setErr(stdErr);
    }

    /**
     * Utility method to add listeners to required fields
     */
    private void addListeners() {

        // listeners for each radio button which hides/shows the table views
        adminSearchRadios.getToggles().forEach(radio -> (radio).selectedProperty().addListener(((observable, oldValue, newValue) -> {
            adminTableView.setVisible(false);
            clinicianTableView.setVisible(false);
            userTableView.setVisible(false);

            if (adminUserRadioButton.isSelected()) {
                userTableView.setVisible(true);
            } else if (adminClinicianRadioButton.isSelected()) {
                clinicianTableView.setVisible(true);
            } else if (adminAdminRadioButton.isSelected()) {
                adminTableView.setVisible(true);
            }

        })));

        userTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchUser(userTableView.getSelectionModel().getSelectedItem());
            }
        });

        clinicianTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                launchClinician(clinicianTableView.getSelectionModel().getSelectedItem());
            }
        });

        adminTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchAdmin(adminTableView.getSelectionModel().getSelectedItem());
            }
        });
    }


    /**
     * Takes a boolean on weather specific fields should be visable and then set them according to the boolean
     *
     * @param shouldSee if the fields should be visible or not
     */
    private void userSpecificFilters(boolean shouldSee) {
        donorStatusLabel.setVisible(shouldSee);
        donorFilterCheckBox.setVisible(shouldSee);
        receiverFilterCheckBox.setVisible(shouldSee);
        allCheckBox.setVisible(shouldSee);
        birthGenderLabel.setVisible(shouldSee);
        genderComboBox.setVisible(shouldSee);
    }


    /**
     * Initialises table for the clinician table
     */
    private void initClinicianSearchTable() {
        ObservableList<Clinician> clinicians = FXCollections.observableArrayList(appController.getClinicians());

        TableColumn<Clinician, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Clinician, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Clinician, String> nhiColumn = new TableColumn<>("Staff Id");
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        lastNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        fListClinicians = new FilteredList<>(clinicians);
        fListClinicians = filter(fListClinicians);
        FilteredList<Clinician> squished = new FilteredList<>(fListClinicians);

        SortedList<Clinician> clinicianSortedList = new SortedList<>(squished);
        clinicianSortedList.comparatorProperty().bind(clinicianTableView.comparatorProperty());
        clinicianTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clinicianTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);
        clinicianTableView.setItems(clinicianSortedList);

    }

    /**
     * Initialises the columns for the admin table
     */
    private void initAdminSearchTable() {
        ObservableList<Administrator> admins = FXCollections
                .observableArrayList(appController.getAdmins());

        endIndex = Math.min(startIndex + ROWS_PER_PAGE, admins.size());
        if (admins.isEmpty()) {
            return;
        }
        TableColumn<Administrator, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Administrator, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Administrator, String> userNameColumn = new TableColumn<>("User Name");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        lastNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        fListAdmins = new FilteredList<>(admins);
        fListAdmins = filter(fListAdmins);
        FilteredList<Administrator> squished = new FilteredList<>(fListAdmins);

        SortedList<Administrator> administratorSortedList = new SortedList<>(squished);
        administratorSortedList.comparatorProperty().bind(adminTableView.comparatorProperty());
        adminTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        adminTableView.getColumns().addAll(userNameColumn, firstNameColumn, lastNameColumn);
        adminTableView.setItems(administratorSortedList);

    }

    /**
     * initialises the search table, abstracted from main init function for clarity
     */
    private void initUserSearchTable() {
        TableColumn<User, String> fNameColumn;
        TableColumn<User, String> dobColumn;
        TableColumn<User, String> dodColumn;
        TableColumn<User, String> ageColumn;
        TableColumn<User, HashSet<Organs>> organsColumn;
        TableColumn<User, String> regionColumn;
        List<User> users = appController.getUsers();

        endIndex = Math.min(startIndex + ROWS_PER_PAGE, users.size());
        if (users.isEmpty()) {
            return;
        }

        List<User> usersSublist = getSearchData(users);
        //set up lists
        //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
        ObservableList<User> oListDonors = FXCollections.observableList(users);

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

        fListUsers = new FilteredList<>(oListDonors);
        fListUsers = filter(fListUsers);
        FilteredList<User> squished = new FilteredList<>(fListUsers);

        SortedList<User> sListUsers = new SortedList<>(squished);
        sListUsers.comparatorProperty().bind(userTableView.comparatorProperty());

        //predicate on this list not working properly
        //should limit the number of items shown to ROWS_PER_PAGE
        //squished = limit(fListDonors, sListDonors);
        //set table columns and contents
        userTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);
        //searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
        userTableView.setItems(sListUsers);
        userTableView.setRowFactory((searchTableView) -> new TooltipTableRow<>(User::getTooltip));

    }

    /**
     * Takes a list and returns a sub section of it.
     * <p>
     * Works for all types
     *
     * @param list A list of any Type.
     * @return A list of supplied type.
     */
    private <T> List<T> getSearchData(List<T> list) {
        return list.subList(startIndex, endIndex);
    }


    /**
     * applies a change listener to the input text box and filters a filtered list accordingly
     *
     * @param toFilter list to be filtered
     * @return filtered list with filter applied
     */
    private <T> FilteredList<T> filter(FilteredList<T> toFilter) {
        setTextFieldListener(adminSearchField, toFilter);
        setTextFieldListener(regionSearchTextField, toFilter);
        setCheckBoxListener(donorFilterCheckBox, toFilter);
        setCheckBoxListener(receiverFilterCheckBox, toFilter);
        setCheckBoxListener(allCheckBox, toFilter);
        genderComboBox.valueProperty()
                .addListener((observable -> setFilteredListPredicate(toFilter)));

        searchTablePagination.setPageCount(searchCount / ROWS_PER_PAGE);
        return toFilter;
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param inputTextField textfield to add the listener to
     * @param filteredList   filteredList  of objects to set predicate property of
     */
    private <T> void setTextFieldListener(TextField inputTextField, FilteredList<T> filteredList) {
        inputTextField.textProperty()
                .addListener((observable) -> setFilteredListPredicate(filteredList));
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param checkBox     checkBox object to add the listener to
     * @param filteredList filteredList of object T to set predicate property of
     */
    private <T> void setCheckBoxListener(CheckBox checkBox, FilteredList<T> filteredList) {
        checkBox.selectedProperty()
                .addListener(((observable) -> setFilteredListPredicate(filteredList)));
    }

    /**
     * Sets the predicate property of filteredList to filter by specific properties
     *
     * @param fList filteredList object to modify the predicate property of
     */
    private <T> void setFilteredListPredicate(FilteredList<T> fList) {
        searchCount = 0; //refresh the searchCount every time so it recalculates it each search
        fList.predicateProperty().bind(Bindings.createObjectBinding(() -> objectToFilter -> {
            String lowerCaseFilterText = adminSearchField.getText().toLowerCase();
            boolean regionMatch = AttributeValidation.checkRegionMatches(regionSearchTextField.getText(), objectToFilter);
            //boolean genderMatch = AttributeValidation.checkGenderMatches(genderComboBox.getValue().toString(), objectToFilter);

            String fName = null;
            String lName = null;
            try {
                fName = (String) objectToFilter.getClass().getMethod("getFirstName").invoke(objectToFilter); //if this breaks just ignore it,
                lName = (String) objectToFilter.getClass().getMethod("getLastName").invoke(objectToFilter); // it will fix itself and not cause problems
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if ((AttributeValidation.checkTextMatches(lowerCaseFilterText, fName) ||
                    AttributeValidation.checkTextMatches(lowerCaseFilterText, lName)) &&
                    (regionMatch)) {// && (genderMatch)) {
                searchCount++;
                return true;
            }/* TODO: reimplement and remove this
             &&
            (((user.isDonor() == donorFilterCheckBox.isSelected()) &&
                    (user.isReceiver() == receiverFilterCheckBox.isSelected())) || allCheckBox.isSelected()))*/
            //if (other test case) return true
            return false;
        }));
        changePage(searchTablePagination.getCurrentPageIndex());
    }

    /**
     * @param pageIndex the current page.
     * @return the search table view node.
     */
    private Node changePage(int pageIndex) {
        startIndex = pageIndex * ROWS_PER_PAGE;
        endIndex = Math.min(startIndex + ROWS_PER_PAGE, appController.getUsers().size());

        int minIndex = Math.min(endIndex, fListUsers.size());

        SortedList<User> sListDonors = new SortedList<>(FXCollections.observableArrayList(fListUsers.subList(Math.min(startIndex, minIndex), minIndex)));
        sListDonors.comparatorProperty().bind(userTableView.comparatorProperty());

        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        userTableView.setItems(sListDonors);


        int pageCount = searchCount / ROWS_PER_PAGE;
        searchTablePagination.setPageCount(pageCount > 0 ? pageCount + 1 : 1);
        searchCountLabel.setText("Showing results " + (searchCount == 0 ? startIndex : startIndex + 1) + " - " + (minIndex) + " of " + searchCount);

        return userTableView;
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
     * Imports admins from a file chosen from a fileselector
     *
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importAdmins() throws FileNotFoundException {
        Log.info("Admin " + administrator.getUserName() + " Importing Administrator profiles");
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");
        boolean invalidFile = false;
        int loadedAdminsAmount;
        if (isAllWindowsClosed()) {
            boolean updated = false;
            Collection<Administrator> existingAdmins = appController.getAdmins();
            String filename;
            filename = FileSelectorController.getFileSelector(stage);
            if (filename != null) {
                fileNotFoundLabel.setVisible(false);
                try {
                    Collection<Administrator> administrators = JsonHandler.loadAdmins(filename);
                    for (Administrator admin : administrators) {
                        if (admin.getUserName() == null) {
                            invalidFile = true;
                            break;
                        }
                        for (Administrator existingAdmin : existingAdmins) {
                            if (admin.getUserName().equals(existingAdmin.getUserName())) {
                                //appController.updateAdmins(admin);
                                updated = true;
                                break;
                            }
                        }
                        if (!updated) {
                            appController.addAdmin(admin);
                        } else {
                            updated = false;
                        }
                    }
                    loadedAdminsAmount = administrators.size();
                } catch (FileNotFoundException e) {
                    Log.severe("File not found", e);
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    throw e;
                }
                if (invalidFile) {
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.warning("Incorrect file loaded - leads to NullPointerException.");
                } else {
                    confirmAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.info("successfully imported " + loadedAdminsAmount + " Admin profiles");
                    System.out.println(loadedAdminsAmount + " admins were successfully loaded.");
                }
            } else {
                Log.warning("File name not found");
                fileNotFoundLabel.setVisible(true);
            }
        } else {
            launchAlertUnclosedWindowsGUI();
        }
    }

    /**
     * Imports clinicians from a file chosen from a fileselector
     *
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importClinicians() throws FileNotFoundException {
        Log.info("Admin " + administrator.getUserName() + " Importing Clinician profiles");
        boolean invalidFile = false;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");
        int loadedCliniciansAmount;
        if (isAllWindowsClosed()) {
            boolean updated = false;
            Collection<Clinician> existingClinicians = appController.getClinicians();
            String filename;
            filename = FileSelectorController.getFileSelector(stage);
            if (filename != null) {
                fileNotFoundLabel.setVisible(false);
                try {
                    Collection<Clinician> clinicians = JsonHandler.loadClinicians(filename);
                    for (Clinician clinician : clinicians) {
                        if (clinician.getStaffId() == null) {
                            invalidFile = true;
                            break;
                        }
                        for (Clinician existingClinician : existingClinicians) {
                            if (clinician.getStaffId().equals(existingClinician.getStaffId())) {
                                appController.updateClinicians(clinician);
                                updated = true;
                                break;
                            }
                        }
                        if (!updated) {
                            appController.addClinician(clinician);
                        } else {
                            updated = false;
                        }
                    }
                    loadedCliniciansAmount = clinicians.size();
                } catch (FileNotFoundException e) {
                    Log.severe("File not found", e);
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    throw e;
                }
                if (invalidFile) {
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.warning("Incorrect file loaded - leads to NullPointerException.");
                } else {
                    confirmAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.info("successfully imported " + loadedCliniciansAmount + " Clinician profiles");
                    System.out.println(loadedCliniciansAmount + " clinicians were successfully loaded.");
                }
            } else {
                Log.warning("File name not found");
                fileNotFoundLabel.setVisible(true);
            }
        } else {
            launchAlertUnclosedWindowsGUI();
        }
    }

    /**
     * Imports Users from a file chosen from a fileselector
     *
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importUsers() throws FileNotFoundException {
        Log.info("Admin " + administrator.getUserName() + " Importing User profiles");
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");
        if (isAllWindowsClosed()) {
            boolean updated = false;
            boolean invalidFile = false;
            int loadedUsersAmount;
            List<User> existingUsers = appController.getUsers();
            String filename;
            filename = FileSelectorController.getFileSelector(stage);
            if (filename != null) {
                try {
                    Collection<User> users = JsonHandler.loadUsers(filename);
                    for (User user : users) {
                        if (user.getNhi() == null) {
                            invalidFile = true;
                            break;
                        } else {
                            for (User existingUser : existingUsers) {
                                if (user.getNhi().equals(existingUser.getNhi())) {
                                    appController.update(user);
                                    updated = true;
                                    break;
                                }
                            }
                        }
                        if (!updated) {
                            appController.addUser(user);
                        } else {
                            updated = false;
                        }
                    }
                    loadedUsersAmount = users.size();
                } catch (FileNotFoundException e) {
                    Log.severe("File not found", e);
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    throw e;
                }
                if (invalidFile) {
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.warning("Incorrect file loaded - leads to NullPointerException.");
                } else {
                    confirmAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.info("successfully imported " + loadedUsersAmount + " Users profiles");
                    System.out.println(loadedUsersAmount + " users were successfully loaded.");
                }
            } else {
                Log.warning("File name not found");
                fileNotFoundLabel.setVisible(true);
            }
        } else {
            launchAlertUnclosedWindowsGUI();
        }

    }

    /**
     * checks if other windows are opened apart from admin overview
     *
     * @return true only if the admin overview is opened, false otherwise
     */
    private boolean isAllWindowsClosed() {
        List<Stage> windows = StageHelper.getStages();
        return windows.size() == 1;
    }

    /**
     * closes all windows apart from admin overview.
     */
    public void CloseAllWindows() {
        List<Stage> windows = StageHelper.getStages();
        int numWindows = windows.size();

        //skips the first stage, which is the admin overview
        for (int i = 1; i < numWindows; i++) {
            windows.get(1).close(); //when close, the stage is removed from list
        }
    }

    /**
     * Launches a popup gui that warns user if there
     * are multiple windows opened.
     */
    private void launchAlertUnclosedWindowsGUI() {
        FXMLLoader AlertUnclosedWindowsLoader = new FXMLLoader(
                getClass().getResource("/FXML/AlertUnclosedWindows.fxml"));
        Parent root;
        try {
            root = AlertUnclosedWindowsLoader.load();
            root.requestFocus(); //Currently the below code thinks that focus = selected so will always take the focused
            // thing in currentDiseases over the selected thing in pastDiseases. Trying to fix
            AlertUnclosedWindowsController alertUnclosedWindowsController = AlertUnclosedWindowsLoader.getController();
            Stage stage = new Stage();
            alertUnclosedWindowsController.init(stage, this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Opens the create user screen
     */
    @FXML
    void addUser() {

        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Create New User Profile");
            newStage.show();
            NewUserController donorController = userLoader.getController();
            donorController.init(AppController.getInstance(), stage, newStage);
            Log.info("Admin " + administrator.getUserName() + " successfully launched create new user window");
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load create new user window", e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the user overview screen for a selected user
     *
     * @param user the selected user.
     */
    @Override
    public void launchUser(User user) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            Collection<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            userController.init(AppController.getInstance(), user, newStage, true, listeners);
            newStage.show();
            Log.info("Admin " + administrator.getUserName() + " successfully launched user overview window for User NHI: " + user.getNhi());
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load user overview window for User NHI: " + user.getNhi(), e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the clinician overview screen for a selected clinician
     *
     * @param clinician the selected clinician.
     */
    private void launchClinician(Clinician clinician) {
        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
        Parent root;
        try {
            root = clinicianLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            ClinicianController clinicianController = clinicianLoader.getController();
            Collection<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            clinicianController.init(newStage, AppController.getInstance(), clinician, owner, listeners);
            //clinicianController.init(newStage, AppController.getInstance(), clinician, true);
            newStage.show();
            Log.info("Admin " + administrator.getUserName() + " successfully launched clinician overview window for Clinician Staff ID:" + clinician.getStaffId());
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load clinician overview window for Clinician Staff ID:" + clinician.getStaffId(), e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the admin overview screen for a selected admin
     *
     * @param administrator the selected administrator
     */
    private void launchAdmin(Administrator administrator) {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/adminView.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            AdministratorViewController adminLoaderController = adminLoader.getController();
            adminLoaderController.init(administrator, AppController.getInstance(), newStage, false, null);
            newStage.show();
            Log.info("Admin " + administrator.getUserName() + " successfully launched administrator overview window");
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load administrator overview window", e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the clinician creation screen
     */
    @FXML
    void addClinician() {

        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
        Parent root;
        try {
            root = clinicianLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateClinicianController newClinician = clinicianLoader.getController();
            newClinician.init(null, appController, stage, true, newStage);
            Log.info("Admin " + administrator.getUserName() + " successfully launched create new clinician window");
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load create new clinician window", e);
            e.printStackTrace();
        }
    }

    /**
     * Launches admin creation screen
     */
    @FXML
    void addAdmin() {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateAdminController updateAdminController = adminLoader.getController();
            updateAdminController.init(new Administrator(), newStage, true);
            Log.info("Admin " + administrator.getUserName() + " successfully launched create new administrator window");
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load create new administrator window", e);
            e.printStackTrace();
        }
    }

    /**
     * Logs out and saves the admin
     */
    @FXML
    void logout() {
        //check about saving
        appController.updateAdmin(administrator);
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loginLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            stage.close();
            LoginController loginController = loginLoader.getController();
            loginController.init(appController, newStage);
            Log.info("Admin " + administrator.getUserName() + " Successfully launched Login window after logout");
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " Failed to load Login window after logout", e);
        }
    }


    /**
     * Undoes the previous action that changed the admin
     */
    @FXML
    void undo() {
        administrator.undo();
        adminUndoButton.setDisable(administrator.getUndoStack().isEmpty());
        displayDetails();
        Log.info("Admin " + administrator.getUserName() + "executed Undo Administrator");
    }

    /**
     * Redoes the previous action that changed the admin
     */
    @FXML
    void redo() {
        administrator.redo();
        adminRedoButton.setDisable(administrator.getRedoStack().isEmpty());
        displayDetails();
        Log.info("Admin " + administrator.getUserName() + "executed Redo Administrator");
    }


    /**
     * load the labels on the admin view with the current admins details
     */
    public void displayDetails() {
        if (!administrator.getUserName().isEmpty()) {
            adminUsernameLable.setText(administrator.getUserName());
            adminFirstnameLabel.setText(administrator.getFirstName());
            if ((administrator.getMiddleName() != null) && !administrator.getMiddleName().isEmpty()) {
                adminMiddleNameLabel.setText(administrator.getMiddleName());
            } else {
                adminMiddleNameLabel.setText("");

            }
            if ((administrator.getLastName() != null) && !administrator.getLastName().isEmpty()) {
                adminLastNameLabel.setText(administrator.getLastName());
            } else {
                adminLastNameLabel.setText("");
            }
        }
        adminUndoButton.setDisable(administrator.getUndoStack().isEmpty());
        adminRedoButton.setDisable(administrator.getRedoStack().isEmpty());
        if (administrator.getChanges().size() > 0) {
            statusBarPageController.updateStatus(administrator.getUserName() + " " + administrator.getChanges().get(administrator.getChanges().size() - 1).getChange());
        }
    }

    /**
     * go to a form to update the admin
     */
    @FXML
    void updateAdmin() {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateAdminController updateAdminController = adminLoader.getController();
            updateAdminController.init(administrator, newStage, false);
            Log.info("Admin " + administrator.getUserName() + " successfully launched update administrator window");
        } catch (IOException e) {
            Log.severe("Admin " + administrator.getUserName() + " failed to load update administrator window", e);
            e.printStackTrace();
        }
    }

    /**
     * Deletes the admin account with a confirmation message
     */
    @FXML
    void deleteAdminAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this administrator?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            appController.deleteAdmin(administrator);
            Log.info("Admin " + administrator.getUserName() + " Successfully deleted Admin account: ");
            if (owner) {
                logout();
            } else {
                stage.close();
            }
        }
    }

    /**
     * Opens a window to restore recently deleted profiles.
     */
    @FXML
    private void openDeletedProfiles() {
        FXMLLoader deletedUserLoader = new FXMLLoader(
                getClass().getResource("/FXML/deletedUsersView.fxml"));
        Parent root;
        try {
            root = deletedUserLoader.load();
            DeletedUserController deletedUserController = deletedUserLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            deletedUserController.init(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            Log.warning(e.getMessage());
        }
    }

    /**
     * updates tables in the admin window with current version of underlying model
     */
    public void refreshTables() {
        transplantWaitListTabPageController.populateWaitListTable();
        adminTableView.refresh();
        clinicianTableView.refresh();
        userTableView.refresh();
    }

    /**
     * event handler that fires when a property change event is emitted by any objects the controller is listening to
     *
     * @param evt event emitted
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //watches users and clinicians
        //refresh view on change
        //if/else not strictly necessary at this stage
        if (evt.getPropertyName().equals(EventTypes.USER_UPDATE.name())) {
            refreshTables();
        } else if (evt.getPropertyName().equals(EventTypes.CLINICIAN_UPDATE.name())) {
            refreshTables();
        }
    }
}