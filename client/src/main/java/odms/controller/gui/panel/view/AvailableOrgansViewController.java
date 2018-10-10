package odms.controller.gui.panel.view;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.ProgressTask;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.widget.CountableLoadingTableView;
import odms.controller.gui.widget.ProgressBarTableCellFactory;
import odms.services.TimeRemainingComparator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class AvailableOrgansViewController {

    @FXML
    private CountableLoadingTableView<AvailableOrganDetail> availableOrgansTableView;

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
    private TableColumn<AvailableOrganDetail, ProgressTask> progressBarColumn;

    @FXML
    private CountableLoadingTableView<TransplantDetails> matchesView;


    private ObservableList<AvailableOrganDetail> availableOrganDetails = FXCollections.observableList(new ArrayList<>());
    private ObservableList<TransplantDetails> transplantDetails = FXCollections.observableList(new ArrayList<>());
    private AvailableOrgansLogicController logicController = new AvailableOrgansLogicController(availableOrganDetails, transplantDetails);
    private PauseTransition pause = new PauseTransition(Duration.millis(300));
    private UserLauncher parent;

    /**
     * Initialises the panel
     *
     * @param parent parent to launch the user from double click from
     */
    @FXML
    public void init(UserLauncher parent) {
        ObservableList<String> organs = FXCollections.observableList(new ArrayList<>());
        organs.add("");
        for (Organs organ : Organs.values()) {
            organs.add(organ.toString());
        }
        this.parent = parent;
        availableOrganFilterComboBox.setItems(organs);
        availableOrganDetails.addListener((ListChangeListener<? super AvailableOrganDetail>) observable -> {
            populateTables();
            while (observable.next()) {
                for (AvailableOrganDetail detail : observable.getAddedSubList()) {
                    detail.addPropertyChangeListener(logicController);
                }
            }
        });
        availableOrganFilterComboBox.valueProperty().addListener(event -> search());
        regionFilterTextField.setOnKeyPressed(event -> {
            pause.setOnFinished(e -> search());
            pause.playFromStart();
        });
        initAvailableOrgansTableView();
        initMatchesTable();
    }

    /**
     * Initialises the available organs table view by binding each column to particular fields contained
     * within the AvailableOrganDetail class
     */
    private void initAvailableOrgansTableView() {
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("donorNhi"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        deathMomentColumn.setCellValueFactory(new PropertyValueFactory<>("momentOfDeath"));
        progressBarColumn.setCellValueFactory(new PropertyValueFactory<>("progressTask"));
        progressBarColumn.setCellFactory(callback -> ProgressBarTableCellFactory.generateCell(progressBarColumn));
        TableColumn<AvailableOrganDetail, String> timeLeftColumn = new TableColumn<>("Time Remaining");
        timeLeftColumn.setCellValueFactory(p -> p.getValue().getProgressTask().messageProperty());
        availableOrgansTableView.getColumns().add(timeLeftColumn);
        availableOrgansTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        availableOrgansTableView.prefHeightProperty().bind(Bindings.size(availableOrgansTableView.getItems()).multiply(availableOrgansTableView.getFixedCellSize()).add(30));
        search();
        populateTables();
        progressBarColumn.setSortType(TableColumn.SortType.ASCENDING);
        progressBarColumn.setComparator(organTimeLeftComparator);
        timeLeftColumn.setComparator(new TimeRemainingComparator());
    }

    private Comparator<ProgressTask> organTimeLeftComparator = Comparator.comparingLong(p -> p.calculateTimeLeft(LocalDateTime.now()));

    /**
     * Initialises the table for potential recipients
     */
    private void initMatchesTable(){
        TableColumn<TransplantDetails,String> matchesNhiColumn = new TableColumn<>("NHI");
        TableColumn<TransplantDetails, String> matchesRegionColumn = new TableColumn<>("Region");

        matchesNhiColumn.setCellValueFactory(new PropertyValueFactory<>("nhi"));
        matchesRegionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        matchesView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn[] tableColumns = {matchesNhiColumn, matchesRegionColumn};
        matchesView.getColumns().addAll(tableColumns);
        matchesView.setItems(transplantDetails);
        matchesView.setPlaceholder(new Label("There are no matches for the selected Organ"));
    }

    /**
     * Sends a search request to the server to fill up the observable list
     */
    @FXML
    public void search() {
        availableOrgansTableView.setWaiting(true);
        logicController.search(0, availableOrganFilterComboBox.getValue(), regionFilterTextField.getText(), availableOrgansTableView);
    }

    /**
     * @see AvailableOrgansLogicController goPrevPage()
     */
    @FXML
    private void goToPreviousPage() {
        logicController.goPrevPage(availableOrgansTableView);
    }

    /**
     * @see AvailableOrgansLogicController goPrevPageMatches()
     */
    @FXML
    private void goToPreviousPageMatches() {
        logicController.goPrevPageMatches(matchesView);
    }

    /**
     * @see AvailableOrgansLogicController goNextPage()
     */
    @FXML
    private void goToNextPage() {
        logicController.goNextPage(availableOrgansTableView);
    }

    /**
     * @see AvailableOrgansLogicController goNextPageMatches()
     */
    @FXML
    private void goToNextPageMatches() {
        logicController.goNextPageMatches(matchesView);
    }


    /**
     * Populates the tables with the items in the availableOrganDetails ObservableList
     */
    private void populateTables() {
        FilteredList<AvailableOrganDetail> filteredAvailableOrganDetails = new FilteredList<>(availableOrganDetails);
        filteredAvailableOrganDetails.setPredicate(AvailableOrganDetail::isOrganStillValid);
        SortedList<AvailableOrganDetail> sortedAvailableOrganDetails = new SortedList<>(filteredAvailableOrganDetails);
        sortedAvailableOrganDetails.comparatorProperty().bind(availableOrgansTableView.comparatorProperty());
        availableOrgansTableView.setItems(sortedAvailableOrganDetails);
        Platform.runLater(() -> availableOrgansTableView.getSortOrder().add(progressBarColumn));
        setOnClickBehaviour();
    }

    /**
     * Binds a double click to each row in the table to launch the user profile
     * Also binds the selection to show potential matches
     */
    private void setOnClickBehaviour() {
        availableOrgansTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && availableOrgansTableView.getSelectionModel().getSelectedItem() != null) {
                parent.launchUser(availableOrgansTableView.getSelectionModel().getSelectedItem().getDonorNhi());
            }
        });
        availableOrgansTableView.getSelectionModel().selectedItemProperty().addListener(a -> {
            if (availableOrgansTableView.getSelectionModel().selectedItemProperty().getValue() != null) {
                matchesView.setWaiting(true);
                logicController.showMatches(availableOrgansTableView.getSelectionModel().getSelectedItem(), matchesView);
            }
        });


        matchesView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && matchesView.getSelectionModel().getSelectedItem() != null){
                parent.launchUser(matchesView.getSelectionModel().getSelectedItem().getNhi());
            }
        });
    }

    /**
     * Shuts down the background threads
     */
    public void shutdownThreads() {
        logicController.shutdownThreads();
    }
}
