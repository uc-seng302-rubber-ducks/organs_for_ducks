package odms.controller.gui.panel.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;

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
        //populate the organs combobox and table columns
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
