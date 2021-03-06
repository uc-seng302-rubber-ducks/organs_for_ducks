package odms.controller.gui.popup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import odms.commons.exception.ProfileAlreadyExistsException;
import odms.commons.exception.ProfileNotFoundException;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._abstract.Deletable;
import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;
import odms.controller.AppController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DeletedUserController {


    public static final String FIRST_NAME = "First name";
    public static final String FIRST_NAME1 = "firstName";
    public static final String LAST_NAME = "Last name";
    public static final String LAST_NAME1 = "lastName";
    public static final String REGION = "Region";
    public static final String REGION1 = "region";
    @FXML
    private TableView<User> deletedUserTableView;

    @FXML
    private TableView<Clinician> deletedClinicianTableView;

    @FXML
    private TableView<Administrator> deletedAdminTableView;


    @FXML
    private ToggleGroup deletedProfileRadioButtons;

    @FXML
    private RadioButton clinicianRadioButton;

    @FXML
    private RadioButton userRadioButton;

    @FXML
    private RadioButton adminRadioButton;

    private ObservableList<User> oListUsers;

    private ObservableList<Clinician> oListClinicians;

    private ObservableList<Administrator> oListAdmins;

    @FXML
    public void init(boolean fromAdmin) {
        initUserTableView();

        if (fromAdmin) {
            deletedProfileRadioButtons.getToggles().forEach(radio -> ((RadioButton) radio).setVisible(true)); // hides radio buttons if not an admin

            // listeners for each radio button which hides/shows the table views
            deletedProfileRadioButtons.getToggles().forEach(radio -> (radio).selectedProperty().addListener(((observable, oldValue, newValue) -> {
                deletedClinicianTableView.setVisible(false);
                deletedAdminTableView.setVisible(false);
                deletedUserTableView.setVisible(false);

                if (userRadioButton.isSelected()) {
                    deletedUserTableView.setVisible(true);

                } else if (clinicianRadioButton.isSelected()) {
                    deletedClinicianTableView.setVisible(true);

                } else if (adminRadioButton.isSelected()) {
                    deletedAdminTableView.setVisible(true);
                }

            })));

            initUserTableView();
            initClinicianTableView();
            initAdminTableView();
        } else {
            initUserTableView();
        }
    }

    /**
     * Populates the table view with public users that have been deleted in the current session.
     */
    private void initUserTableView() {
        oListUsers = FXCollections
                .observableList(new ArrayList<>(AppController.getInstance().getUsers().stream().filter(Deletable::isDeleted).collect(Collectors.toList())));

        TableColumn<User, String> fNameColumn = new TableColumn<>(FIRST_NAME);
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>(FIRST_NAME1));
        fNameColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(7));

        TableColumn<User, String> lNameColumn = new TableColumn<>(LAST_NAME);
        lNameColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_NAME1));
        lNameColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(7));
        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<User, LocalDate> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        dobColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(6));

        TableColumn<User, LocalDate> dodColumn = new TableColumn<>("Date of Death");
        dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));
        dodColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(6));

        TableColumn<User, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(10));

        TableColumn<User, String> regionColumn = new TableColumn<>(REGION);
        regionColumn.setCellValueFactory(new PropertyValueFactory<>(REGION1));
        regionColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(8));

        TableColumn<User, ArrayList<Organs>> organsColumn = new TableColumn<>("Organs");
        organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));
        organsColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(7));

        deletedUserTableView.getColumns()
                .setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn,
                        organsColumn);
        deletedUserTableView.setItems(oListUsers);
    }


    /**
     * Populates the table view with clinicians that have been deleted in the current session.
     */
    private void initClinicianTableView() {
        oListClinicians = FXCollections.observableList(new ArrayList<>(AppController.getInstance().getClinicians()
                .stream().filter(Deletable::isDeleted).collect(Collectors.toList())));

        TableColumn<Clinician, String> staffIDColumn = new TableColumn<>("Staff ID");
        staffIDColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        staffIDColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));

        TableColumn<Clinician, String> fNameColumn = new TableColumn<>(FIRST_NAME);
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>(FIRST_NAME1));
        fNameColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));

        TableColumn<Clinician, String> lNameColumn = new TableColumn<>(LAST_NAME);
        lNameColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_NAME1));
        lNameColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));
        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<Clinician, String> regionColumn = new TableColumn<>(REGION);
        regionColumn.setCellValueFactory(new PropertyValueFactory<>(REGION1));
        regionColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));

        deletedClinicianTableView.getColumns()
                .setAll(staffIDColumn, fNameColumn, lNameColumn, regionColumn);

        deletedClinicianTableView.setItems(oListClinicians);
    }

    /**
     * Populates the table view with administrators that have been deleted in the current session.
     */
    private void initAdminTableView() {
        oListAdmins = FXCollections.observableList(new ArrayList<>(AppController.getInstance().getAdmins()
                .stream().filter(Deletable::isDeleted).collect(Collectors.toList())));

        TableColumn<Administrator, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        usernameColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));

        TableColumn<Administrator, String> fNameColumn = new TableColumn<>(FIRST_NAME);
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>(FIRST_NAME1));
        fNameColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));

        TableColumn<Administrator, String> lNameColumn = new TableColumn<>(LAST_NAME);
        lNameColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_NAME1));
        lNameColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));
        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<Administrator, String> regionColumn = new TableColumn<>(REGION);
        regionColumn.setCellValueFactory(new PropertyValueFactory<>(REGION1));
        regionColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));


        deletedAdminTableView.getColumns()
                .setAll(usernameColumn, fNameColumn, lNameColumn, regionColumn);

        deletedAdminTableView.setItems(oListAdmins);
    }


    /**
     * Attempts to restore either a user, clinician or administrator depending on which radio button is currently selected.
     * Displays a message with the result of the attempt.   *
     */
    @FXML
    private void undoDeletedProfile() {
        try {
            if (userRadioButton.isSelected()) {
                AppController.getInstance()
                        .undoDeletion(deletedUserTableView.selectionModelProperty().getValue().getSelectedItem());
                oListUsers.remove(deletedUserTableView.selectionModelProperty().getValue().getSelectedItem());

            } else if (clinicianRadioButton.isSelected()) {
                AppController.getInstance()
                        .undoClinicianDeletion(deletedClinicianTableView.selectionModelProperty().getValue().getSelectedItem());
                oListClinicians.remove(deletedClinicianTableView.selectionModelProperty().getValue().getSelectedItem());

            } else if (adminRadioButton.isSelected()) {
                AppController.getInstance()
                        .undoAdminDeletion(deletedAdminTableView.selectionModelProperty().getValue().getSelectedItem());
                oListAdmins.remove(deletedAdminTableView.selectionModelProperty().getValue().getSelectedItem());
            }

            displayMessage("Profile successfully restored!");
            Log.info("Successfully restored profile");

        } catch (ProfileAlreadyExistsException | ProfileNotFoundException e) {
            displayMessage(e.getMessage());
            Log.severe("Unable to restore deleted profile", e);
        }
    }

    /**
     * Displays an alert message to the user.
     *
     * @param message the message to be displayed
     */
    private void displayMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }
}
