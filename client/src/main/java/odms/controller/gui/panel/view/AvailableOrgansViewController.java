package odms.controller.gui.panel.view;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.utils.ProgressTask;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.widget.ProgressBarTableCellFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

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
    private TableColumn<AvailableOrganDetail, ProgressTask> progressBarColumn;


    private ObservableList<AvailableOrganDetail> availableOrganDetails = FXCollections.observableList(new ArrayList<>());
    private AvailableOrgansLogicController logicController = new AvailableOrgansLogicController(availableOrganDetails);
    private SortedList<AvailableOrganDetail> sortedAvailableOrganDetails;
    private PauseTransition pause = new PauseTransition(Duration.millis(300));
    private UserLauncher parent;

    @FXML
    public void init(UserLauncher parent) {
        ObservableList<String> organs = FXCollections.observableList(new ArrayList<>());
        organs.add("");
        for (Organs organ : Organs.values()) {
            organs.add(organ.toString());
        }
        this.parent = parent;
        availableOrganFilterComboBox.setItems(organs);
        availableOrganDetails.addListener((ListChangeListener<? super AvailableOrganDetail>) (observable) -> {
            populateTables();
            while (observable.next()) {
                for (AvailableOrganDetail detail : observable.getAddedSubList()) {
                    detail.addPropertyChangeListener(logicController);
                }
            }
        });
        availableOrganFilterComboBox.valueProperty().addListener(event -> search());
        regionFilterTextField.setOnKeyPressed(event -> {
            availableOrganDetails.add(new AvailableOrganDetail(Organs.LIVER, "", null, "", "", 0));
            pause.setOnFinished(e -> search());
            pause.playFromStart();
        });
        initAvailableOrgansTableView();
    }

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
        // figure out how to do progress bars
        search();
        populateTables();
        progressBarColumn.setSortType(TableColumn.SortType.ASCENDING);
        progressBarColumn.setComparator(organTimeLeftComparator);
    }

    private Comparator<ProgressTask> organTimeLeftComparator = Comparator.comparingLong(p -> p.calculateTimeLeft(LocalDateTime.now()));


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

    private void populateTables() {
        FilteredList<AvailableOrganDetail> filteredAvailableOrganDetails = new FilteredList<>(availableOrganDetails);
        filteredAvailableOrganDetails.setPredicate(AvailableOrganDetail::isOrganStillValid);
        sortedAvailableOrganDetails = new SortedList<>(filteredAvailableOrganDetails);
        sortedAvailableOrganDetails.comparatorProperty().bind(availableOrgansTableView.comparatorProperty());
        availableOrgansTableView.setItems(sortedAvailableOrganDetails);
        availableOrgansTableView.getSortOrder().add(progressBarColumn); //TODO:need to find a way for tableview to execute the organ time remaining comparator when table is fully initialised rather than executing the comparator of first column (Donor column) - 12/8
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
