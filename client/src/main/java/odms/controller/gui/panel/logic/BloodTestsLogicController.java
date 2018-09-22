package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.view.NewBloodTestViewController;

import java.io.IOException;
import java.time.LocalDate;

public class BloodTestsLogicController {

    private ObservableList<BloodTest> bloodTests;
    private ObservableList<BloodTest> graphBloodTests;
    private static final int ROWS_PER_PAGE = 30;
    private static final int RESULTS_ON_GRAPH = 50;
    private int startingIndex = 0;
    private User user;

    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param bloodTests  observable list of BloodTest to use to populate the Blood tests table
     */
    public BloodTestsLogicController(ObservableList<BloodTest> bloodTests, ObservableList<BloodTest> graphBloodTests, User user) {
        this.bloodTests = bloodTests;
        this.graphBloodTests = graphBloodTests;
        this.user = user;
    }

    public void addNewBloodTest() {
        FXMLLoader newBloodTestLoader = new FXMLLoader(getClass().getResource("/FXML/BloodTestPopUP.fxml"));
        Parent root;

        try {
            root = newBloodTestLoader.load();
            NewBloodTestViewController newBloodTestViewController = newBloodTestLoader.getController();
            Stage bloodTestStage = new Stage();
            newBloodTestViewController.init(user,bloodTestStage);
            bloodTestStage.setScene(new Scene(root));
            bloodTestStage.showAndWait();
            Log.info("Successfully launched the new blood test pop-up window for user: " + user.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load new blood test pop-up window for user: " + user.getNhi(), e);
        }

    }

    public void deleteBloodTest(BloodTest bloodTest) {
        user.getHealthDetails().getBloodTests().remove(bloodTest);
    }

    public void updateBloodTest() {

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
}
