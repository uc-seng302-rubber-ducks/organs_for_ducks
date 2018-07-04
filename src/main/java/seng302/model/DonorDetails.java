package seng302.model;

import com.google.gson.annotations.Expose;
import seng302.model._enum.Organs;

import java.util.HashSet;
import java.util.Set;


/**
 * Class to track details for a user
 */
public class DonorDetails {

    @Expose
    private Set<Organs> organs;
    private transient User attachedUser;

    /**
     * Constructor for organs for current user
     *
     * @param attachedUser current user
     */
    DonorDetails(User attachedUser) {
        this.attachedUser = attachedUser;
        this.organs = new HashSet<>();
    }

    /**
     * initialises organs
     */
    public void initOrgans() {
        organs = new HashSet<>();
    }

    public void setOrgans(Set<Organs> organs) {
        attachedUser.updateLastModified();
        this.organs = organs;
    }

    public Set<Organs> getOrgans() {
        return organs;
    }

    /**
     * Adds an organ to the user profile. If the added organ is not already in the set, this will be
     * counted as an action performed by the attachedUser
     *
     * @param organ the organ to be added (from Organs enum)
     */
    public void addOrgan(Organs organ) { //The previous logic on this seemed like it had had an initial goal but had
        // been edited multiple times and never looked at as a whole
        if (organs == null) {
            organs = new HashSet<>();
        }

        if (attachedUser != null) {
            attachedUser.saveStateForUndo();
            attachedUser.updateLastModified();
            attachedUser.addChange(new Change("Added organ " + organ.toString()));

            boolean changed = this.organs.add(organ);

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
        if (organs.contains(organ)) {
            attachedUser.saveStateForUndo();
            organs.remove(organ);
            attachedUser.updateLastModified();
            attachedUser.addChange(new Change("Removed organ " + organ.organName));
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
}
