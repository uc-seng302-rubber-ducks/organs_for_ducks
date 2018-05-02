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
   * Adds an organ to the user profile.
   *
   * @param organ the enum of organs.
   */
  public void addOrgan(Organs organ) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(attachedUser.clone());
    if (attachedUser != null){
      attachedUser.updateLastModified();
    }
    if (organs == null) {
      organs = new HashSet<>();
      organs.add(organ);
    }
    this.organs.add(organ);
    attachedUser.updateLastModified();
    memento.setNewObject(attachedUser.clone());
    attachedUser.getUndoStack().push(memento);
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
    }
    memento.setNewObject(attachedUser.clone());
    attachedUser.getUndoStack().push(memento);
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
   * USE THIS SPARINGLY. can easily cause consistency issues user.getDonorDetails().setAttachedUser(user)
   * is the only sensible use case and is a hack at best.
   *
   * @param attachedUser user the
   */
  public void setAttachedUser(User attachedUser) {
    this.attachedUser = attachedUser;
  }

  public User getAttachedUser() {
    return attachedUser;
  }
}
