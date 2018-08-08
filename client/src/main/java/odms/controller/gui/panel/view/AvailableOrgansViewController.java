package odms.controller.gui.panel.view;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
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
    private TableColumn<AvailableOrganDetail, ProgressBarTableCell<LocalDateTime>> progressBarColumn;

    private AvailableOrgansLogicController logicController = new AvailableOrgansLogicController();

    @FXML
    public void init() {
        ObservableList<String> organs = FXCollections.observableList(new ArrayList<>());
        for (Organs organ : Organs.values()) {
            organs.add(organ.toString());
        }
        initAvailableOrgansTableView();
    }

    private void initAvailableOrgansTableView() {
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("donorNhi"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        deathMomentColumn.setCellValueFactory(new PropertyValueFactory<>("momentOfDeath"));
        // figure out how to do progress bars
        // initialise table columns here
    }


    @FXML
    private void search() {
        // Call logicController.search
    }

    @FXML
    private void goToPreviousPage() {
        // Call logic controller's things
    }

    @FXML
    private void goToNextPage() {
        // Call logic controller's things
    }
}
