package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.gui.panel.logic.BloodTestsLogicController;

import java.time.LocalDate;
import java.util.ArrayList;

public class BloodTestViewController {
    @FXML
    private TableView<BloodTest> bloodTestView;
    @FXML
    private TextField redBloodCount;
    @FXML
    private TextField whiteBloodCount;
    @FXML
    private TextField heamoglobin;
    @FXML
    private  TextField platelets;
    @FXML
    private TextField glucose;
    @FXML
    private TextField meanCellVolume;
    @FXML
    private TextField haematocrit;
    @FXML
    private TextField meanCellHaematocrit;
    @FXML
    private TableColumn<BloodTest, LocalDate> dateRequestedColumn;

    private ObservableList<BloodTest> bloodTests = FXCollections.observableList(new ArrayList<>());
    private BloodTestsLogicController logicController = new BloodTestsLogicController(bloodTests);
    private User user;

    @FXML
    public void init(User user) {
        this.user = user;
        initBloodTestTableView();
    }

    private void populateTables() {
        SortedList<BloodTest> bloodTestSortedList = new SortedList<>(bloodTests);
    }

    @FXML
    private void updateBloodTest() {

    }

    @FXML
    private void deleteBloodTest() {

    }

    @FXML
    private void goToNextPage() {

    }

    @FXML
    private void goToPreviousPage() {

    }

    @FXML
    private void addNewBloodTest() {

    }

    private void initBloodTestTableView() {
        dateRequestedColumn.setCellValueFactory((new PropertyValueFactory<>("")));
        bloodTestView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        populateTables();
    }
}
