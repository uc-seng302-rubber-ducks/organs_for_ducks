package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.AppController;

import java.time.LocalDate;

public class BloodTestsLogicController {

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
    }

    public void updateTableView(int start) {
        bloodTests.clear();
        String startDate = LocalDate.now().minusYears(1).toString();
        String endDate = LocalDate.now().toString();
        AppController.getInstance().getBloodTestBridge().getBloodTests(user.getNhi(), startDate, endDate, ROWS_PER_PAGE, start, bloodTests);
    }

}
