package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.bridge.BloodTestBridge;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.view.NewBloodTestViewController;

import java.io.IOException;
import java.time.LocalDate;

public class BloodTestsLogicController {

    private ObservableList<BloodTest> bloodTests;
    private static final int ROWS_PER_PAGE = 30;
    private int startingIndex = 0;
    private User user;
    private BloodTestBridge bloodTestBridge;


    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param bloodTests  observable list of BloodTest to use to populate the Blood tests table
     */
    public BloodTestsLogicController(ObservableList<BloodTest> bloodTests, User user, AppController controller) {
        this.bloodTests = bloodTests;
        this.user = user;
        bloodTestBridge = controller.getBloodTestBridge();

    }

    public void addNewBloodTest() {
        FXMLLoader newBloodTestLoader = new FXMLLoader(getClass().getResource("/FXML/BloodTestPopUP.fxml"));
        Parent root;

        try {
            root = newBloodTestLoader.load();
            NewBloodTestViewController newBloodTestViewController = newBloodTestLoader.getController();
            Stage bloodTestStage = new Stage();
            newBloodTestViewController.init(user,bloodTestStage, bloodTestBridge);
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
}
