package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import odms.commons.model.Change;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.popup.view.ProcedureModificationViewController;
import odms.controller.gui.window.UserController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ProcedureTabController {
    public static final String FOR_USER_NHI = " for User NHI: ";


    @FXML
    private Button removeProcedureButton;

    @FXML
    private Button addProcedureButton;


    @FXML
    private TableView<MedicalProcedure> previousProcedureTableView;

    @FXML
    private TableView<MedicalProcedure> pendingProcedureTableView;


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

        previousProcedures = FXCollections.observableArrayList();
        pendingProcedures = FXCollections.observableArrayList();
        pendingProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        previousProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        moveSelectedProcedureTo(previousProcedureTableView, pendingProcedureTableView);
        moveSelectedProcedureTo(pendingProcedureTableView, previousProcedureTableView);
        constructTables();
        if (!fromClinician) {
            addProcedureButton.setVisible(false);
            removeProcedureButton.setVisible(false);
        }

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
        previousProcedureTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openProceduresPopUp(previousProcedureTableView.getSelectionModel().getSelectedItem());
            }
        });

        pendingProcedureTableView.setOnMouseClicked(event -> {
            if(event.getClickCount() ==2) {
                openProceduresPopUp(pendingProcedureTableView.getSelectionModel().getSelectedItem());
            }
        });

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
                        addProcedureButton.setVisible(false);
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
        openProceduresPopUp(null);
        clearProcedure();
        application.update(currentUser);
        parent.updateUndoRedoButtons();

        previousProcedureTableView.refresh();
        pendingProcedureTableView.refresh();
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
     * Clears the information of a shown procedure
     */
    @FXML
    void clearProcedure() {
        pendingProcedureTableView.getSelectionModel().select(null);
        previousProcedureTableView.getSelectionModel().select(null);
        currentProcedureList = null;
        addProcedureButton.setVisible(true);
        Log.info("Successfully cleared procedure for User NHI: " + currentUser.getNhi());
    }

    /**
     * Removes a procedure from the current users profile
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

    private void openProceduresPopUp(MedicalProcedure procedure){
        FXMLLoader procedureModificationLoader = new FXMLLoader(
                getClass().getResource("/FXML/proceduresPopUp.fxml"));
        Parent root;
        try {
            root = procedureModificationLoader.load();
            ProcedureModificationViewController procedureModificationViewController = procedureModificationLoader.getController();
            Stage stage = new Stage();
            procedureModificationViewController.init(procedure, stage, currentUser);
            stage.setScene(new Scene(root));
            stage.show();
            Log.info("successfully launched add procedures pop-up window for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("failed to load add procedures pop-up window for User NHI: " + currentUser.getNhi(), e);
            AlertWindowFactory.generateError("Whoops! Something went wrong!");
        }
    }
}
