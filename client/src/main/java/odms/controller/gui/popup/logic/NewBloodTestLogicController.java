package odms.controller.gui.popup.logic;

import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;

import java.time.LocalDate;

public class NewBloodTestLogicController {
    private User user;
    private Stage stage;


    public NewBloodTestLogicController(User user, Stage stage){
        this.user = user;
        this.stage = stage;
    }

    /**
     * closes the new blood test view
     */
    public void cancel() {
        stage.close();
    }


    public void addBloodTest(LocalDate date, String redBloodCount, String whiteBloodCount, String heamoglobin, String platelets, String  glucose, String meanCellVolume, String haematocrit, String meanCellHaematocrit) {
        BloodTest bloodTest = new BloodTest(Double.parseDouble(redBloodCount),Double.parseDouble(whiteBloodCount), Double.parseDouble(heamoglobin),
                Double.parseDouble(platelets), Double.parseDouble(glucose),Double.parseDouble(meanCellVolume),
                Double.parseDouble(haematocrit),Double.parseDouble(meanCellHaematocrit),date);
        user.getHealthDetails().getBloodTests().add(bloodTest);
        stage.close();


    }
}
