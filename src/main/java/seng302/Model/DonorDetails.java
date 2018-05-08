package seng302.Model;

import com.google.gson.annotations.Expose;
import java.util.HashSet;


/**
 * Class to track details for a donor
 */
public class DonorDetails {

  @Expose
  private HashSet<Organs> organs = new HashSet<>();
  private transient User attachedUser;

  public HashSet<Organs> getOrgans() {
    return organs;
  }

  /**
   * Constructor for organs for current user
   *
   * @param attachedUser current user
   */
  public DonorDetails(User attachedUser) {
    this.attachedUser = attachedUser;
    this.organs = new HashSet<>();
  }

  /**
   * initialises organs
   */
  public void initOrgans() {
    organs = new HashSet<>();
  }

  public void setOrgans(HashSet<Organs> organs) {
    attachedUser.updateLastModified();
    this.organs = organs;
  }

  /**
   * Adds an organ to the user profile. If the added organ is not already in the set, this will be
   * counted as an action performed by the attachedUser
   *
   * @param organ the organ to be added (from Organs enum)
   */
  public void addOrgan(Organs organ) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(attachedUser.clone());
    if (attachedUser != null) {
      attachedUser.updateLastModified();
      attachedUser.addChange(new Change("Added organ " + organ.toString()));
    }
    if (organs == null) {
      organs = new HashSet<>();
    }
    boolean changed = this.organs.add(organ);

    attachedUser.updateLastModified();
    attachedUser.updateLastModified();
    if (changed) {
      memento.setNewObject(attachedUser.clone());
      attachedUser.getUndoStack().push(memento);
    }
  }

  /**
   * Removes an organ from the user profile.
   *
   * @param organ the enum of organs.
   */
  public void removeOrgan(Organs organ) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(attachedUser.clone());
    if (organs.contains(organ)) {
      organs.remove(organ);
      //TODO attachedUser is always null
      attachedUser.updateLastModified();
      attachedUser.addChange(new Change("Removed organ " + organ.organName));
    }
    memento.setNewObject(attachedUser.clone());
    attachedUser.getUndoStack().push(memento);
  }

  private boolean isCurrentlyWaitingFor(Organs organ) {
    return attachedUser.getReceiverDetails().isCurrentlyWaitingFor(organ);
  }

  /**
   * TODO update if/when more details are added
   *
   * @return true if underlying organs list is empty
   */
  public boolean isEmpty() {
    if (organs != null) {
      return organs.isEmpty();
    }
    return true;
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
