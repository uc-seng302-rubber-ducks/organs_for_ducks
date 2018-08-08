package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import odms.commons.model._enum.Organs;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;

import java.util.ArrayList;

public class AvailableOrgansViewController {

    @FXML
    private TableView availableOrgansTableView;

    @FXML
    private ComboBox availableOrganFilterComboBox;

    @FXML
    private TextField regionFilterComboBox;

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
