package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import odms.commons.model.User;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.gui.panel.logic.BloodTestsLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BloodTestViewController {

    @FXML
    private Button requestNewBloodTest;
    @FXML
    private Button updateBloodTest;
    @FXML
    private Button deleteBloodTest;

    @FXML
    private DatePicker bloodTestDatePicker;
    @FXML
    private TextField redBloodCount;
    @FXML
    private TextField whiteBloodCount;
    @FXML
    private TextField heamoglobin;
    @FXML
    private TextField platelets;
    @FXML
    private TextField glucose;
    @FXML
    private TextField meanCellVolume;
    @FXML
    private TextField haematocrit;
    @FXML
    private TextField meanCellHaematocrit;

    @FXML
    private Label bloodTestTitle;
    @FXML
    private Label bloodTestDateLabel;
    @FXML
    private Label bloodTestRCCountLabel;
    @FXML
    private Label bloodTestWCCountLabel;
    @FXML
    private Label bloodTestHeamoglobinLabel;
    @FXML
    private Label bloodTestPlateletsLabel;
    @FXML
    private Label bloodTestGlucoseLabel;
    @FXML
    private Label bloodTestMCVolumeLabel;
    @FXML
    private Label bloodTestHaematocritLabel;
    @FXML
    private Label bloodTestMCHaematocritLabel;

    @FXML
    private Toggle bloodTestTableToggle;
    @FXML
    private Toggle bloodTestGraphToggle;

    @FXML
    private ComboBox<String> timeRangeFilterOption;

    @FXML
    private LineChart<String, Double> bloodTestGraph;
    @FXML
    private CategoryAxis timeRangeAxis;
    @FXML
    private NumberAxis bloodTestPropertyAxis;

    @FXML
    private AnchorPane bloodTestTableViewPane;
    @FXML
    private AnchorPane bloodTestDetailsPane;
    @FXML
    private AnchorPane bloodTestGraphViewPane;
    @FXML
    private AnchorPane bloodTestGraphFilterPane;

    @FXML
    private TableView<BloodTest> bloodTestTableView;
    @FXML
    private TableColumn<BloodTest, LocalDate> testDateColumn;
    @FXML
    private TableColumn<BloodTest, List<BloodTestProperties>> lowPropertyValuesColumn;
    @FXML
    private TableColumn<BloodTest, List<BloodTestProperties>> highPropertyValuesColumn;

    private ObservableList<BloodTest> bloodTests = FXCollections.observableList(new ArrayList<>());
    private ObservableList<BloodTest> graphBloodTests = FXCollections.observableList(new ArrayList<>());
    private ObservableList<String> timeRangeCategory = FXCollections.observableList(new ArrayList<>());
    private BloodTestsLogicController logicController;
    private final int MONTH_IN_WEEKS = 4;
    private boolean fromClinician;

    /**
     * Initializes the blood test tab on the given users profile
     *
     * @param user The current user
     */
    public void init(User user, boolean fromClinician) {
        this.fromClinician = fromClinician;

        bloodTests.addListener((ListChangeListener<? super BloodTest>) observable -> {
            populateTable();
        });

        graphBloodTests.addListener((ListChangeListener<? super BloodTest>) observable -> {
            populateGraph();
        });

        if (fromClinician) {
            showFields();
        }

        logicController = new BloodTestsLogicController(bloodTests, graphBloodTests, user);
        initBloodTestTableView();
        initGraphView();
    }

    /**
     * Makes the extra fields visible to the clinicians so blood tests can be created, updated and deleted.
     */
    private void showFields() {
        requestNewBloodTest.setVisible(true);
        updateBloodTest.setVisible(true);
        deleteBloodTest.setVisible(true);
        bloodTestDatePicker.setVisible(true);
        redBloodCount.setVisible(true);
        whiteBloodCount.setVisible(true);
        heamoglobin.setVisible(true);
        platelets.setVisible(true);
        glucose.setVisible(true);
        haematocrit.setVisible(true);
        meanCellVolume.setVisible(true);
        meanCellHaematocrit.setVisible(true);
    }

    /**
     * Initializes the table view of blood tests for the current user
     */
    private void initBloodTestTableView() {
        testDateColumn.setCellValueFactory(new PropertyValueFactory<>("testDate"));
        lowPropertyValuesColumn.setCellValueFactory(new PropertyValueFactory<>("lowValues"));
        highPropertyValuesColumn.setCellValueFactory(new PropertyValueFactory<>("highValues"));
        logicController.updateTableView(0);
        populateTable();
        setClickOnBehaviour();
    }

    /**
     * Initializes the graph view of blood tests for the current user
     */
    private void initGraphView() {
        timeRangeAxis.setAutoRanging(false);
        timeRangeAxis.setCategories(timeRangeCategory);
    }

    /**
     * Populates the table view with blood tests retrieved from the database
     */
    private void populateTable() {
        bloodTestTableView.setItems(bloodTests);
    }

    /**
     * Binds the table view row selection to show all details for the selected blood test
     */
    private void setClickOnBehaviour() {
        bloodTestTableView.getSelectionModel().selectedItemProperty().addListener(a -> {
            BloodTest selectedBloodTest = bloodTestTableView.getSelectionModel().getSelectedItem();

            if (selectedBloodTest != null) {
                displayBloodTestDetails(selectedBloodTest);
            } else {
                clearDetails();
            }
        });
    }

    /**
     * Displays the given blood test in more detail
     * The details are displayed as labels for users and text fields for clinicians/admins
     *
     * @param selectedBloodTest The selected blood test to be displayed in more detail
     */
    private void displayBloodTestDetails(BloodTest selectedBloodTest) {
        if (fromClinician) {
            bloodTestDatePicker.setValue(selectedBloodTest.getTestDate());
            redBloodCount.setText(getString(selectedBloodTest.getRedBloodCellCount()));
            whiteBloodCount.setText(getString(selectedBloodTest.getWhiteBloodCellCount()));
            heamoglobin.setText(getString(selectedBloodTest.getHaemoglobinLevel()));
            platelets.setText(getString(selectedBloodTest.getPlatelets()));
            glucose.setText(getString(selectedBloodTest.getGlucoseLevels()));
            haematocrit.setText(getString(selectedBloodTest.getHaematocrit()));
            meanCellVolume.setText(getString(selectedBloodTest.getMeanCellVolume()));
            meanCellHaematocrit.setText(getString(selectedBloodTest.getMeanCellHaematocrit()));

        } else {
            bloodTestDateLabel.setText(selectedBloodTest.getTestDate().toString());
            bloodTestRCCountLabel.setText(getString(selectedBloodTest.getRedBloodCellCount()));
            bloodTestWCCountLabel.setText(getString(selectedBloodTest.getWhiteBloodCellCount()));
            bloodTestHeamoglobinLabel.setText(getString(selectedBloodTest.getHaemoglobinLevel()));
            bloodTestPlateletsLabel.setText(getString(selectedBloodTest.getPlatelets()));
            bloodTestGlucoseLabel.setText(getString(selectedBloodTest.getGlucoseLevels()));
            bloodTestMCVolumeLabel.setText(getString(selectedBloodTest.getMeanCellVolume()));
            bloodTestHaematocritLabel.setText(getString(selectedBloodTest.getHaematocrit()));
            bloodTestMCHaematocritLabel.setText(getString(selectedBloodTest.getMeanCellHaematocrit()));
        }
    }

    /**
     * Used to find the appropriate string to set the text field to.
     * If the given value is 0.0, the text field is set to the empty string.
     *
     * @param value The value of a blood test property
     * @return The given value as a string if not 0.0, otherwise an empty string
     */
    private String getString(double value) {
        if (value == 0.0) {
            if (fromClinician) {
                return "";
            } else {
                return "This property was not tested";
            }
        }

        return Double.toString(value);
    }

    /**
     * Clears the labels and text fields when no blood test is selected
     */
    private void clearDetails() {
        if (fromClinician) {
            bloodTestDatePicker.setValue(null);
            redBloodCount.setText("");
            whiteBloodCount.setText("");
            heamoglobin.setText("");
            platelets.setText("");
            glucose.setText("");
            haematocrit.setText("");
            meanCellVolume.setText("");
            meanCellHaematocrit.setText("");
        } else {
            bloodTestDateLabel.setText("");
            bloodTestRCCountLabel.setText("");
            bloodTestWCCountLabel.setText("");
            bloodTestHeamoglobinLabel.setText("");
            bloodTestPlateletsLabel.setText("");
            bloodTestGlucoseLabel.setText("");
            bloodTestMCVolumeLabel.setText("");
            bloodTestHaematocritLabel.setText("");
            bloodTestMCHaematocritLabel.setText("");
        }
    }

    @FXML
    private void bloodTestTableGraphToggle() {
        if (bloodTestTableToggle.isSelected()) {
            bloodTestTitle.setText("Blood Test Entries");
            bloodTestGraphFilterPane.setVisible(false);
            bloodTestGraphViewPane.setVisible(false);
            bloodTestDetailsPane.setVisible(true);
            bloodTestTableViewPane.setVisible(true);

        } else if (bloodTestGraphToggle.isSelected()) {
            bloodTestTitle.setText("Blood Test Statistics");
            bloodTestGraphFilterPane.setVisible(true);
            bloodTestGraphViewPane.setVisible(true);
            bloodTestDetailsPane.setVisible(false);
            bloodTestTableViewPane.setVisible(false);
        }
    }

    /**
     * Updates the graph
     */
    @FXML
    private void updateGraph() {
        changeLabels();
        logicController.updateGraph(timeRangeFilterOption.getValue());
        populateGraph();
    }

    /**
     * Populates the graph with blood test properties specified by the filters
     */
    private void populateGraph() {
        bloodTestGraph.getData().removeAll(bloodTestGraph.getData());
        createGraphSeries();
    }

    /**
     * Creates a series containing the specified property and the time frame to populate the graph
     */
    private void createGraphSeries() {
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        XYChart.Data<String, Double> data;
        for (BloodTest bloodTest : graphBloodTests) {
            data = new XYChart.Data<>(bloodTest.getTestDate().getDayOfWeek().name(), bloodTest.getRedBloodCellCount());
            series.getData().add(data);
        }

        bloodTestGraph.getData().add(series);
    }


    /**
     * Changes the graph title and axis' depending on the filter options
     */
    private void changeLabels() {
        Collection<String> timeRange;
        switch (timeRangeFilterOption.getValue()) {
            case "Day":
                bloodTestTitle.setText("Property over the current Day");
                timeRangeAxis.setLabel("Time in hours");
                break;

            case "Week":
                bloodTestTitle.setText("Property over the current Week");
                timeRangeAxis.setLabel("Time in days");
                timeRange = Stream.of(DayOfWeek.values()).map(DayOfWeek::name).collect(Collectors.toList());
                changeTimeRange(timeRange);
                break;

            case "Fortnight":
                bloodTestTitle.setText("Property over the current Fortnight");
                timeRangeAxis.setLabel("Time in days");
                break;

            case "Month":
                bloodTestTitle.setText("Property over the past Month");
                timeRangeAxis.setLabel("Time in weeks");
                timeRange = new ArrayList<>();
                for (int i = MONTH_IN_WEEKS - 1; i >= 0; i -= 1) {
                    timeRange.add(LocalDate.now().minusWeeks(i).toString());
                }

                changeTimeRange(timeRange);
                break;

            case "Year":
                bloodTestTitle.setText("Property over the current Year");
                timeRangeAxis.setLabel("Time in months");
                timeRange = Stream.of(Month.values()).map(Month::name).collect(Collectors.toList());
                changeTimeRange(timeRange);
                break;

            default:
                break;
        }
    }

    /**
     * Sets the x-axis of the graph to have fixed values for either the days of the week, the week of the month,
     * or the month of the year. These are given through a collection.
     *
     * @param categories Collection containing the categories for the time range.
     */
    private void changeTimeRange(Collection<String> categories) {
        timeRangeCategory.clear();
        timeRangeCategory.addAll(categories);
    }

    @FXML
    private void updateBloodTest() {
        logicController.updateBloodTest();

    }

    @FXML
    private void deleteBloodTest() {
        if (bloodTestTableView.getSelectionModel().getSelectedItem() != null) {
            logicController.deleteBloodTest(bloodTestTableView.getSelectionModel().getSelectedItem());
        } else {
            AlertWindowFactory.generateInfoWindow("You must select an blood test to delete");
        }
    }

    @FXML
    private void goToNextPage() {
        logicController.gotoNextPage();

    }

    @FXML
    private void goToPreviousPage() {
        logicController.goToPreviousPage();

    }

    @FXML
    private void addNewBloodTest() {
        logicController.addNewBloodTest();
    }


}
