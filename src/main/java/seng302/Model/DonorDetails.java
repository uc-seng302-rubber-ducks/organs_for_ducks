package seng302.Model;

import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class DonorDetails {
  @Expose
  private HashSet<Organs> organs = new HashSet<>();
  private transient User attachedUser;

  public HashSet<Organs> getOrgans() {
    return organs;
  }

  public DonorDetails(User attachedUser) {
    this.attachedUser = attachedUser;
  }

  public void initOrgans() {
    organs = new HashSet<>();
  }

  public void setOrgans(HashSet<Organs> organs) {
    attachedUser.updateLastModified();
    this.organs = organs;
  }

  /**
   * Adds an organ to the user profile.
   * @param organ the enum of organs.
   */
  public void addOrgan(Organs organ) {
    if (organs == null) {
      organs = new HashSet<>();
      organs.add(organ);
    }
    this.organs.add(organ);
    //TODO attachedUser is always null
    //attachedUser.updateLastModified();
  }

  /**
   * Removes an organ from the user profile.
   * @param organ the enum of organs.
   */
  public void removeOrgan(Organs organ) {
    if (organs.contains(organ)) {
      organs.remove(organ);
      //TODO attachedUser is always null
      //attachedUser.updateLastModified();
    }
  }

  /**
   * TODO update if/when more details are added
   * @return true if underlying organs list is empty
   */
  public boolean isEmpty() {
    if (organs != null) {
      return organs.isEmpty();
    }
    return true;
  }
}
