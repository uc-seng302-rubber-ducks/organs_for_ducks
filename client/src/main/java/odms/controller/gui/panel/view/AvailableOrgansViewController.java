package odms.controller.gui.panel.view;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.utils.ProgressTask;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;

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
    private TableColumn<ProgressTask, Double> progressBarColumn;


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
            availableOrganDetails.add(new AvailableOrganDetail(Organs.LIVER, "", null, "", ""));
//            pause.setOnFinished(e -> search());
//            pause.playFromStart();
        });
        initAvailableOrgansTableView();
    }

    private void initAvailableOrgansTableView() {
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("donorNhi"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        deathMomentColumn.setCellValueFactory(new PropertyValueFactory<>("momentOfDeath"));
        progressBarColumn.setCellValueFactory(new PropertyValueFactory<>("progressBar"));
        progressBarColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
        // figure out how to do progress bars
        search();
        populateTables();
    }


    @FXML
    private void search() {
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
        availableOrgansTableView.setItems(availableOrganDetails);
        setOnClickBehaviour();
    }

    private void setOnClickBehaviour() {
        availableOrgansTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                parent.launchUser(availableOrgansTableView.getSelectionModel().getSelectedItem().getDonorNhi());
            }
        });
    }
}
