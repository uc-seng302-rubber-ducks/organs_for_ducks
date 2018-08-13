package odms.commons.model;

import com.google.gson.annotations.Expose;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ExpiryReason;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Class to track details for a user
 */
public class DonorDetails {

    @Expose
    private Map<Organs, ExpiryReason> organs;
    private transient User attachedUser; //NOSONAR

    /**
     * Constructor for organs for current user
     *
     * @param attachedUser current user
     */
    DonorDetails(User attachedUser) {
        this.attachedUser = attachedUser;
        this.organs = new HashMap<>();
    }

    public void setOrgans(Map<Organs, ExpiryReason> organs) {
        attachedUser.updateLastModified();
        this.organs = organs;
    }

    public Map<Organs, ExpiryReason> getOrganMap() {
        return organs;
    }

    public Set<Organs> getOrgans() {
        return organs.keySet();
    }

    /**
     * Adds an organ to donate and an ExpiryReason to the user profile.
     * The ExpiryReason will only be given if the donor is dead and the organ has been manually expired,
     * otherwise it is null
     *
     * @param organ the Organ enum to be added
     * @param reason the ExpiryReason object for manually expiring the given organ
     */
    public void addOrgan(Organs organ, ExpiryReason reason) { //The previous logic on this seemed like it had had an initial goal but had
        // been edited multiple times and never looked at as a whole
        if (organs == null) {
            organs = new HashMap<>();
        }

        if (attachedUser != null) {
            attachedUser.saveStateForUndo();
            attachedUser.updateLastModified();
            attachedUser.addChange(new Change("Added organ " + organ.toString()));

            boolean changed = false;

            if (reason == null) {
                reason = new ExpiryReason();
            }

            if (!organs.containsKey(organ)) {
                organs.put(organ, reason);
                changed = true;
            } else if (!organs.get(organ).equals(reason)) {
                organs.replace(organ, reason);
                changed = true;
            }

            if (changed) {
                attachedUser.getRedoStack().clear();
            } else {
                attachedUser.getUndoStack().pop();
            }
        }
    }

    /**
     * Removes an organ from the user profile.
     *
     * @param organ the enum of organs.
     */
    public void removeOrgan(Organs organ) {
        if (organs.containsKey(organ)) {
            attachedUser.saveStateForUndo();
            organs.remove(organ);
            attachedUser.updateLastModified();
            attachedUser.addChange(new Change("Removed organ " + organ.toString()));
        }
    }

    /**
     * @return true if underlying organs list is empty
     */
    public boolean isEmpty() {
        return organs == null || organs.isEmpty();
    }

    /**
     * USE SPARINGLY. this can easily create consistency issues. Only sensible use case is
     * user.getDonorDetails().setAttachedUser(user)
     *
     * @param attachedUser user to connect
     */
    public void setAttachedUser(User attachedUser) {
        this.attachedUser = attachedUser;
    }

    public User getAttachedUser() {
        return attachedUser;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Organs to donate:\n");
        Set<Organs> organSet = organs.keySet();
        for (Organs o : organSet){
            sb.append(o).append("\n");
        }
        return sb.toString();
    }
}
