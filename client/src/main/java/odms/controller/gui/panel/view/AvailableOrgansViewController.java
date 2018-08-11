package odms.controller.gui.panel.view;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.utils.ProgressBarService;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.widget.ProgressBarTableCellFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AvailableOrgansViewController {

    @FXML
    private TableView<AvailableOrganDetail> availableOrgansTableView;

    @FXML
    private ComboBox<String> availableOrganFilterComboBox;

    @FXML
    private TextField regionFilterTextField;

    @FXML
    private TableColumn<AvailableOrganDetail, String> nhiColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, String> regionColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, String> organColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, LocalDateTime> deathMomentColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, ProgressBarService> progressBarColumn;


    private ObservableList<AvailableOrganDetail> availableOrganDetails = FXCollections.observableList(new ArrayList<>());
    private AvailableOrgansLogicController logicController = new AvailableOrgansLogicController(availableOrganDetails);
    private PauseTransition pause = new PauseTransition(Duration.millis(300));
    private UserLauncher parent;

    @FXML
    public void init(UserLauncher parent) {
        ObservableList<String> organs = FXCollections.observableList(new ArrayList<>());
        for (Organs organ : Organs.values()) {
            organs.add(organ.toString());
        }
        this.parent = parent;
        availableOrganFilterComboBox.setItems(organs);
        availableOrganDetails.addListener((ListChangeListener<? super AvailableOrganDetail>) observable -> populateTables());
        regionFilterTextField.setOnKeyPressed(event -> {
            pause.setOnFinished(e -> search());
            pause.playFromStart();
        });
        initAvailableOrgansTableView();
    }

    /**
     * Binds each column of the available organs table to particular fields contained
     * within the AvailableOrganDetail class
     */
    public void initAvailableOrgansTableView() {
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("donorNhi"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        deathMomentColumn.setCellValueFactory(new PropertyValueFactory<>("momentOfDeath"));
        progressBarColumn.setCellValueFactory(new PropertyValueFactory<>("progressTask"));
        progressBarColumn.setCellFactory(callback -> ProgressBarTableCellFactory.generateCell(progressBarColumn));
        // figure out how to do progress bars
        search();
        populateTables();
        availableOrgansTableView.setItems(availableOrganDetails);
    }

    @FXML
    public void search() {
        logicController.search(0, availableOrganFilterComboBox.getValue(), regionFilterTextField.getText());
    }

    @FXML
    private void goToPreviousPage() {
        logicController.goPrevPage();
    }

    @FXML
    private void goToNextPage() {
        logicController.goNextPage();
    }

    public void populateTables() {
        setOnClickBehaviour();
    }

    private void setOnClickBehaviour() {
        availableOrgansTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && availableOrgansTableView.getSelectionModel().getSelectedItem() != null) {
                parent.launchUser(availableOrgansTableView.getSelectionModel().getSelectedItem().getDonorNhi());
            }
        });
    }

    public void shutdownThreads() {
        logicController.shutdownThreads();
    }
}
