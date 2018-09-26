package odms.controller.gui.popup.logic;

import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.AppController;

import java.time.LocalDate;

public class NewBloodTestLogicController {
    private User user;
    private Stage stage;

    /**
     * Initializes the pop-up to create a new blood test
     *
     * @param user  The user who the blood test is for
     * @param stage The new stage
     */
    public NewBloodTestLogicController(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
    }

    /**
     * closes the new blood test view
     */
    public void cancel() {
        stage.close();
    }


    /**
     * A method to add a new blood test to a user
     *
     * @param date the blood test happened
     * @param redBloodCount the blood test's red blood count
     * @param whiteBloodCount the blood test's white blood count
     * @param heamoglobin the blood test's heamoglobin level
     * @param platelets the blood test's platelets level
     * @param glucose the blood test's glucose level
     * @param meanCellVolume the blood test's meanCellVolume level
     * @param haematocrit the blood test's haematocrit level
     * @param meanCellHaematocrit the blood test's meanCellHaematocrit level
     */
    public void addBloodTest(LocalDate date, String redBloodCount, String whiteBloodCount, String heamoglobin,
                             String platelets, String  glucose, String meanCellVolume, String haematocrit,
                             String meanCellHaematocrit) {
        if (redBloodCount.isEmpty()) {
            redBloodCount = "0.0";
        }
        if (whiteBloodCount.isEmpty()) {
            whiteBloodCount = "0.0";
        }
        if (heamoglobin.isEmpty()) {
            heamoglobin = "0.0";
        }
        if (platelets.isEmpty()) {
            platelets = "0.0";
        }
        if (glucose.isEmpty()) {
            glucose = "0.0";
        }
        if (meanCellVolume.isEmpty()){
            meanCellVolume = "0.0";
        }
        if (haematocrit.isEmpty()) {
            haematocrit = "0.0";
        }
        if (meanCellHaematocrit.isEmpty()) {
            meanCellHaematocrit = "0.0";
        }
        BloodTest bloodTest = new BloodTest(Double.parseDouble(redBloodCount),Double.parseDouble(whiteBloodCount), Double.parseDouble(heamoglobin),
                Double.parseDouble(platelets), Double.parseDouble(glucose),Double.parseDouble(meanCellVolume),
                Double.parseDouble(haematocrit),Double.parseDouble(meanCellHaematocrit),date);
        AppController appController = AppController.getInstance();
        appController.getBloodTestBridge().postBloodTest(bloodTest, user.getNhi(), appController.getToken());
        stage.close();
    }
}
