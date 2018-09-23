package odms.controller.gui.popup.logic;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.AttributeValidation;

public class RemoveDisqualificationLogicController {

    private User user;
    private Stage stage;
    private ObservableList<OrgansWithDisqualification> disqualifications;


    public RemoveDisqualificationLogicController(User user, Stage stage, ObservableList<OrgansWithDisqualification> disqualifiedOrgans) {
        this.user = user;
        this.stage = stage;
        this.disqualifications = disqualifiedOrgans;
    }

    /**
     * Confirms the disqualification of an organ
     */
    public void confirm(OrgansWithDisqualification disqualifiedOrgan, String description) {

        disqualifiedOrgan.setReason(description);
        disqualifiedOrgan.setCurrentlyDisqualified(false);
//        for (int i = 0; i < disqualifications.size(); i++) {
//            if (disqualifications.get(i).getOrganType().equals(disqualifiedOrgan.getOrganType())) {
//                disqualifications.remove(i);
//                disqualifications.add(i, disqualifiedOrgan);
//                break;
//            }
//        }

        stage.close();
    }

    /**
     * Validates a description string for disqualified organs
     * @param description of the disqualified organ
     * @return true if the description is empty, true otherwise
     */
    public boolean validateDescription(String description) {
        return AttributeValidation.checkRequiredString(description);
    }


    /**
     * closes the Disqualify Organ Reason view.
     */
    public void cancel() {
        stage.close();
    }
}
