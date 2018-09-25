package odms.controller.gui.popup.logic;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.AttributeValidation;

public class RemoveDisqualificationLogicController {

    private User user;
    private ObservableList<OrgansWithDisqualification> disqualifications;


    public RemoveDisqualificationLogicController(User user, ObservableList<OrgansWithDisqualification> disqualifiedOrgans) {
        this.user = user;
        this.disqualifications = disqualifiedOrgans;
    }

    /**
     * Confirms the disqualification of an organ
     */
    public void confirm(OrgansWithDisqualification disqualifiedOrgan, String description) {
        user.saveStateForUndo();
        disqualifiedOrgan.setReason(description);
        disqualifiedOrgan.setCurrentlyDisqualified(false);
        for (int i = 0; i < disqualifications.size(); i++) {
            if (disqualifications.get(i).getOrganType().equals(disqualifiedOrgan.getOrganType())) {
                disqualifications.remove(i);
                disqualifications.add(i, disqualifiedOrgan);
                user.getUndoStack().pop();
                user.getUndoStack().pop();
                break;
            }
        }
    }

    /**
     * Validates a description string for disqualified organs
     * @param description of the disqualified organ
     * @return true if the description is empty, true otherwise
     */
    public boolean validateDescription(String description) {
        return AttributeValidation.checkRequiredString(description);
    }

}
