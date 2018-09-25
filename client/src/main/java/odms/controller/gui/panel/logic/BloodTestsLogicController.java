package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

public class BloodTestsLogicController implements PropertyChangeListener {

    private ObservableList<BloodTest> bloodTests;
    private static final int ROWS_PER_PAGE = 30;
    private int startingIndex = 0;
    private User user;

    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param bloodTests  observable list of BloodTest to use to populate the Blood tests table
     */
    public BloodTestsLogicController(ObservableList<BloodTest> bloodTests, User user) {
        this.bloodTests = bloodTests;
        this.user = user;
        ServerEventNotifier.getInstance().addPropertyChangeListener(this);
    }

    /**
     * Opens the new blood test pop up for users to add a new blood test
     */
    public void addNewBloodTest() {
        FXMLLoader newBloodTestLoader = new FXMLLoader(getClass().getResource("/FXML/BloodTestPopUP.fxml"));
        Parent root;

        try {
            root = newBloodTestLoader.load();
            NewBloodTestViewController newBloodTestViewController = newBloodTestLoader.getController();
            Stage bloodTestStage = new Stage();
            newBloodTestViewController.init(user,bloodTestStage, AppController.getInstance().getBloodTestBridge());
            bloodTestStage.setScene(new Scene(root));
            bloodTestStage.setResizable(false);
            bloodTestStage.setTitle("Add New Blood Test");
            bloodTestStage.showAndWait();
            Log.info("Successfully launched the new blood test pop-up window for user: " + user.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load new blood test pop-up window for user: " + user.getNhi(), e);
        }

    }

    /**
     * Calls the database to delete the given blood test
     * @param bloodTest the blood test to be deleted
     */
    public void deleteBloodTest(BloodTest bloodTest) {
        AppController appController = AppController.getInstance();
        appController.getBloodTestBridge().deleteBloodTest(Integer.toString(bloodTest.getBloodTestId()),
                user.getNhi(), appController.getToken());
    }

    /**
     * Calls the database to update the given blood entry
     * @param bloodTest the blood test to be updated
     */
    public void updateBloodTest(BloodTest bloodTest) {
        AppController appController = AppController.getInstance();
        appController.getBloodTestBridge().patchBloodTest(bloodTest, user.getNhi(), appController.getToken());
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
        if (bloodTests.size() < ROWS_PER_PAGE) {
            return;
        }
        startingIndex = startingIndex + ROWS_PER_PAGE;
        updateTableView(startingIndex);
    }

    public void goToPreviousPage() {
        if (startingIndex - ROWS_PER_PAGE < 0) {
            return;
        }
        startingIndex = startingIndex - ROWS_PER_PAGE;
        updateTableView(startingIndex);
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
}
