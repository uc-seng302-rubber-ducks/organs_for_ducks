package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.bridge.BloodTestBridge;
import odms.commons.model.User;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.view.NewBloodTestViewController;
import odms.socket.ServerEventNotifier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class BloodTestsLogicController implements PropertyChangeListener {

    private ObservableList<BloodTest> bloodTests;
    private ObservableList<BloodTest> graphBloodTests;
    private static final int ROWS_PER_PAGE = 30;
    private static final int RESULTS_ON_GRAPH = 365;
    private int startingIndex = 0;
    private User user;
    private BloodTestBridge bloodTestBridge;


    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param bloodTests  observable list of BloodTest to use to populate the Blood tests table
     */
    public BloodTestsLogicController(ObservableList<BloodTest> bloodTests, ObservableList<BloodTest> graphBloodTests, User user) {
        this.bloodTests = bloodTests;
        this.graphBloodTests = graphBloodTests;
        this.user = user;
        bloodTestBridge = AppController.getInstance().getBloodTestBridge();
        ServerEventNotifier.getInstance().addPropertyChangeListener(this);

    }

    public void addNewBloodTest() {
        FXMLLoader newBloodTestLoader = new FXMLLoader(getClass().getResource("/FXML/BloodTestPopUP.fxml"));
        Parent root;

        try {
            root = newBloodTestLoader.load();
            NewBloodTestViewController newBloodTestViewController = newBloodTestLoader.getController();
            Stage bloodTestStage = new Stage();
            newBloodTestViewController.init(user, bloodTestStage, bloodTestBridge);
            bloodTestStage.setScene(new Scene(root));
            bloodTestStage.showAndWait();
            Log.info("Successfully launched the new blood test pop-up window for user: " + user.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load new blood test pop-up window for user: " + user.getNhi(), e);
        }

    }

    public void deleteBloodTest(BloodTest bloodTest) {
        bloodTestBridge.deleteBloodtest(Integer.toString(bloodTest.getBloodTestId()), user.getNhi());
    }

    public void updateBloodTest(BloodTest bloodTest) {
        bloodTestBridge.patchBloodtest(bloodTest, user.getNhi());
    }

    /**
     * Calls the database to get updated blood test entries
     *
     * @param start The entry number for the database that the table will start from
     */
    public void updateTableView(int start) {
        bloodTests.clear();
        String startDate = LocalDate.now().minusYears(100).toString();
        String endDate = LocalDate.now().toString();
        AppController.getInstance().getBloodTestBridge().getBloodTests(user.getNhi(), startDate, endDate, ROWS_PER_PAGE, start, bloodTests);
    }

    public void gotoNextPage() {

    }

    public void goToPreviousPage() {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        UpdateNotificationEvent event;
        try {
            event = (UpdateNotificationEvent) evt;
        } catch (ClassCastException ex) {
            return;
        }
        if (event == null) {
            return;
        }
        if (event.getType().equals(EventTypes.BLOOD_TEST_UPDATE)) {
            updateTableView(startingIndex);
        }
    }

    /**
     * Finds the appropriate start date from the time range
     *
     * @param timeRange The time range to filter the graph by
     * @return      The start date as a string
     */
    private String findStartDate(String timeRange) {
        String startDate = "";
        switch (timeRange) {
            case "Day":
                startDate = LocalDate.now().minusDays(1).toString();
                break;

            case "Week":
                startDate = LocalDate.now().minusWeeks(1).toString();
                break;

            case "Fortnight":
                startDate = LocalDate.now().minusWeeks(2).toString();
                break;

            case "Month":
                startDate = LocalDate.now().minusMonths(1).toString();
                break;

            case "Year":
                startDate = LocalDate.now().minusYears(1).toString();
                break;

            default:
                break;
        }
        return startDate;
    }

    /**
     * Calls the database to retrieve blood test entries with the filter options to populate the graph
     *
     * @param timeRange The time range to display blood test results from on the graph
     */
    public void updateGraph(String timeRange) {
        graphBloodTests.clear();
        String startDate = findStartDate(timeRange);
        String endDate = LocalDate.now().toString();
        AppController.getInstance().getBloodTestBridge().getBloodTests(user.getNhi(), startDate, endDate, RESULTS_ON_GRAPH, 0, graphBloodTests);
    }

    /**
     * Gets the date value of the given blood test as a String depending on the selected time range.
     * Week and year options are returned with the english word value for the days and months,
     * whereas month and fortnight options are returned with dates.
     *
     * @param bloodTest The current blood test to gather data from
     * @param timeRange The selected time frame for the graph
     * @return The date of the blood test as a string corresponding to the format of the selected time range
     */
    public String changeValuesBasedOnTimeRange(BloodTest bloodTest, String timeRange) {
        String testDate = "";
        switch (timeRange) {
            case "Week":
                testDate = bloodTest.getTestDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                break;

            case "Fortnight":
                testDate = bloodTest.getTestDate().toString();
                break;

            case "Month":
                testDate = bloodTest.getTestDate().toString();
                break;

            case "Year":
                testDate = bloodTest.getTestDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                break;

            default:
                break;
        }
        return testDate;
    }
}
