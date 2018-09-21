package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import odms.commons.model.datamodel.BloodTest;

public class BloodTestsLogicController {
    private ObservableList<BloodTest> bloodTests;


    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param bloodTests  observable list of BloodTest to use to populate the Blood tests table
     */
    public BloodTestsLogicController(ObservableList<BloodTest> bloodTests) {
        this.bloodTests = bloodTests;
    }



}
