package odms.controller.gui.popup.logic;

import javafx.stage.Stage;
import odms.bridge.BloodTestBridge;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;

import java.time.LocalDate;

public class NewBloodTestLogicController {
    private User user;
    private Stage stage;
    private BloodTestBridge bloodTestBridge;


    public NewBloodTestLogicController(User user, Stage stage, BloodTestBridge bloodTestBridge){
        this.user = user;
        this.stage = stage;
        this.bloodTestBridge = bloodTestBridge;
    }

    /**
     * closes the new blood test view
     */
    public void cancel() {
        stage.close();
    }


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
        bloodTestBridge.postBloodtest(bloodTest, user.getNhi());
        stage.close();


    }
}
