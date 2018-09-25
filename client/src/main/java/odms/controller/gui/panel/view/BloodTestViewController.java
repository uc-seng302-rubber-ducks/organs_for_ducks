package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import odms.commons.model.User;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.AttributeValidation;
import odms.controller.gui.panel.logic.BloodTestsLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.widget.TextStringRadioButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

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
    private ListView<TextStringRadioButton> bloodTestPropertyListView;
    @FXML
    private ComboBox<String> timeRangeFilterOption;

    @FXML
    private LineChart<String, Double> bloodTestGraph;
    @FXML
    private CategoryAxis timeRangeAxis;

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
    private boolean fromClinician;
    private BloodTest bloodTest;

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
     * Initializes the graph view of blood tests for the current user.
     * Removing the auto ranging allows the time range axis to have fixed values depending on the current category.
     */
    private void initGraphView() {
        timeRangeAxis.setAutoRanging(false);
        timeRangeAxis.setCategories(timeRangeCategory);
        bloodTestGraph.setLegendVisible(false);

        final ToggleGroup toggleGroup = new ToggleGroup();
        ObservableList<TextStringRadioButton> bloodTestProperties = FXCollections.observableList(new ArrayList<>());
        for (BloodTestProperties btp : BloodTestProperties.values()) {
            TextStringRadioButton radioButton = new TextStringRadioButton(btp.toString());
            radioButton.selectedProperty().addListener(a -> updateGraph());
            bloodTestProperties.add(radioButton);
            radioButton.setToggleGroup(toggleGroup);
        }

        bloodTestPropertyListView.setItems(bloodTestProperties);

        timeRangeFilterOption.getSelectionModel().selectedItemProperty().addListener(a -> updateGraph());
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
                bloodTest = selectedBloodTest;
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

    /**
     * Toggles between the blood test table view and the graph view
     */
    @FXML
    private void bloodTestTableGraphToggle() {
        if (bloodTestTableToggle.isSelected()) {
            bloodTestTitle.setText("Blood Test Entries");
            bloodTestGraphFilterPane.setVisible(false);
            bloodTestGraphViewPane.setVisible(false);
            bloodTestDetailsPane.setVisible(true);
            bloodTestTableViewPane.setVisible(true);
            requestNewBloodTest.setVisible(true);

        } else if (bloodTestGraphToggle.isSelected()) {
            bloodTestTitle.setText("Blood Test Statistics");
            bloodTestGraphFilterPane.setVisible(true);
            bloodTestGraphViewPane.setVisible(true);
            bloodTestDetailsPane.setVisible(false);
            bloodTestTableViewPane.setVisible(false);
            requestNewBloodTest.setVisible(false);
        }
    }

    /**
     * Updates the graph
     */
    private void updateGraph() {
        changeLabels();
        logicController.updateGraph(timeRangeFilterOption.getValue());
    }

    /**
     * Populates the graph with blood test properties specified by the filters
     */
    private void populateGraph() {
        bloodTestGraph.getData().removeAll(bloodTestGraph.getData());
        ObservableList<TextStringRadioButton> items = bloodTestPropertyListView.getItems();
        for (TextStringRadioButton item : items) {
            if (item.isSelected()) {
                createGraphSeries(BloodTestProperties.valueOf(item.getText().replaceAll(" ", "_").toUpperCase()));
                return;
            }
        }
    }

    /**
     * Creates a series containing the specified property and the time frame to populate the graph
     */
    private void createGraphSeries(BloodTestProperties property) {
        if (graphBloodTests.size() != 0) {
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            for (BloodTest bT : graphBloodTests) {
                addAppropriateProperty(bT, series, property);
            }
            bloodTestGraph.getData().add(series);
        }
    }

    /**
     * Adds the given blood test property to the chart series.
     *
     * @param bT        The current blood test to gather data from
     * @param series    The current series being created and applied to the graph
     * @param property  The blood test property to be displayed on the chart series
     */
    private void addAppropriateProperty(BloodTest bT, XYChart.Series<String, Double> series, BloodTestProperties property) {
        String date = logicController.changeValuesBasedOnTimeRange(bT, timeRangeFilterOption.getValue());
        double value = 0.0;

        if (property == BloodTestProperties.RED_BLOOD_CELL) {
            value = bT.getRedBloodCellCount();
        } else if (property == BloodTestProperties.WHITE_BLOOD_CELL) {
            value = bT.getWhiteBloodCellCount();
        } else if (property == BloodTestProperties.GLUCOSE) {
            value = bT.getGlucoseLevels();
        } else if (property == BloodTestProperties.HAEMATOCRIT) {
            value = bT.getHaematocrit();
        } else if (property == BloodTestProperties.HAEMOGLOBIN) {
            value = bT.getHaemoglobinLevel();
        } else if (property == BloodTestProperties.MEAN_CELL_HAEMATOCRIT) {
            value = bT.getMeanCellHaematocrit();
        } else if (property == BloodTestProperties.MEAN_CELL_VOLUME) {
            value = bT.getMeanCellVolume();
        } else if (property == BloodTestProperties.PLATELETS) {
            value = bT.getPlatelets();
        }

        if (value != 0.0) {
            XYChart.Data<String, Double> data = new XYChart.Data<>(date, value);
            series.getData().add(data);
        }
    }

    /**
     * Changes the graph title and axis' depending on the filter options
     */
    private void changeLabels() {
        Collection<String> timeRange;
        switch (timeRangeFilterOption.getValue()) {
            case "Week":
                bloodTestGraph.setTitle("Results Over the Current Week");
                timeRangeAxis.setLabel("Time in Days");
                timeRangeAxis.setTickLabelRotation(0);
                DayOfWeek start = LocalDate.now().minusWeeks(1).getDayOfWeek();
                List<DayOfWeek> days = Arrays.asList(DayOfWeek.values());

                days.sort((o1, o2) -> {
                    int o1Value = o1.getValue();
                    if (o1Value < start.getValue()) {
                        o1Value += 7;
                    }
                    int o2Value = o2.getValue();
                    if (o2Value < start.getValue()) {
                        o2Value += 7;
                    }

                    return Integer.compare(o1Value, o2Value);
                });

                timeRange = new ArrayList<>();
                days.forEach(day -> timeRange.add(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)));

                changeTimeRange(timeRange);
                break;

            case "Fortnight":
                bloodTestGraph.setTitle("Results Over the Current Fortnight");
                timeRangeAxis.setLabel("Time in Days");
                timeRangeAxis.setTickLabelRotation(-45);
                timeRange = new ArrayList<>();
                LocalDate now = LocalDate.now();

                int daysInAFortnight = 14;
                for (int i = daysInAFortnight - 1; i >= 0; i -= 1) {
                    timeRange.add(now.minusDays(i).toString());
                }

                changeTimeRange(timeRange);
                break;

            case "Month":
                timeRangeAxis.setVisible(false);
                bloodTestGraph.setTitle("Results Over the Past Month");
                timeRangeAxis.setLabel("Time in Days");
                timeRangeAxis.setTickLabelRotation(-45);
                timeRange = new ArrayList<>();
                LocalDate currentDate = LocalDate.now();

                int daysInAMonth = 30;
                for (int i = daysInAMonth - 1; i >= 0; i -= 1) {
                    timeRange.add(currentDate.minusDays(i).toString());
                }
                changeTimeRange(timeRange);
                break;

            case "Year":
                bloodTestGraph.setTitle("Results Over the Current Year");
                timeRangeAxis.setLabel("Time in months");
                timeRangeAxis.setTickLabelRotation(0);
                Month startMonth = LocalDate.now().minusWeeks(1).getMonth();
                List<Month> months = Arrays.asList(Month.values());

                months.sort((o1, o2) -> {
                    int o1Value = o1.getValue();
                    if (o1Value < startMonth.getValue()) {
                        o1Value += 12;
                    }
                    int o2Value = o2.getValue();
                    if (o2Value < startMonth.getValue()) {
                        o2Value += 12;
                    }

                    return Integer.compare(o1Value, o2Value);
                });

                timeRange = new ArrayList<>();
                months.forEach(month -> timeRange.add(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)));
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

    private void invalidateNode(Node node) {
        node.getStyleClass().add("invalid");
    }


    private boolean validateField() {
        boolean valid = true;
        if (AttributeValidation.validateDouble(redBloodCount.getText()) == -1) {
            bloodTestRCCountLabel.setVisible(true);
            invalidateNode(redBloodCount);
            valid = false;
        }
        if (AttributeValidation.validateDouble(whiteBloodCount.getText()) == -1) {
            bloodTestWCCountLabel.setVisible(true);
            invalidateNode(whiteBloodCount);
            valid = false;

        }
        if (AttributeValidation.validateDouble(heamoglobin.getText()) == -1) {
            bloodTestHeamoglobinLabel.setVisible(true);
            invalidateNode(heamoglobin);
            valid = false;
        }
        if (AttributeValidation.validateDouble(platelets.getText()) == -1) {
            bloodTestPlateletsLabel.setVisible(true);
            invalidateNode(platelets);
            valid = false;
        }
        if (AttributeValidation.validateDouble(glucose.getText()) == -1) {
            bloodTestGlucoseLabel.setVisible(true);
            invalidateNode(glucose);
            valid = false;
        }
        if (AttributeValidation.validateDouble(meanCellVolume.getText()) == -1) {
            bloodTestMCVolumeLabel.setVisible(true);
            invalidateNode(meanCellVolume);
            valid = false;
        }
        if (AttributeValidation.validateDouble(haematocrit.getText()) == -1) {
            bloodTestHaematocritLabel.setVisible(true);
            invalidateNode(haematocrit);
            valid = false;
        }
        if (AttributeValidation.validateDouble(meanCellHaematocrit.getText()) == -1) {
            bloodTestMCHaematocritLabel.setVisible(true);
            invalidateNode(meanCellHaematocrit);
            valid = false;
        }
        if (!AttributeValidation.validateDateBeforeTomorrow(bloodTestDatePicker.getValue())) {
            bloodTestDateLabel.setVisible(true);
            invalidateNode(bloodTestDatePicker);
            valid = false;
        }
        return valid;
    }

    @FXML
    private void updateBloodTest() {
            if (validateField()) {
                bloodTest.setGlucoseLevels(AttributeValidation.validateDouble(glucose.getText()));
                bloodTest.setHaematocrit(AttributeValidation.validateDouble(haematocrit.getText()));
                bloodTest.setMeanCellHaematocrit(AttributeValidation.validateDouble(meanCellHaematocrit.getText()));
                bloodTest.setPlatelets(AttributeValidation.validateDouble(platelets.getText()));
                bloodTest.setWhiteBloodCellCount(AttributeValidation.validateDouble(whiteBloodCount.getText()));
                bloodTest.setRedBloodCellCount(AttributeValidation.validateDouble(redBloodCount.getText()));
                bloodTest.setMeanCellVolume(AttributeValidation.validateDouble(meanCellVolume.getText()));
                bloodTest.setHaemoglobinLevel(AttributeValidation.validateDouble(heamoglobin.getText()));
                bloodTest.setTestDate(bloodTestDatePicker.getValue());
                logicController.updateBloodTest(bloodTest);
                AlertWindowFactory.generateInfoWindow("Blood Test on: "+ bloodTest.getTestDate() +" updated");
            }
    }

    @FXML
    private void deleteBloodTest() {
        if (bloodTestTableView.getSelectionModel().getSelectedItem() != null) {
            Optional<ButtonType> result = AlertWindowFactory.generateConfirmation("Are you sure you want to delete this blood test?");

            if (!result.isPresent()) {
            return;
        }

            if (result.get() == ButtonType.OK) {
                logicController.deleteBloodTest(bloodTestTableView.getSelectionModel().getSelectedItem());
            }
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
