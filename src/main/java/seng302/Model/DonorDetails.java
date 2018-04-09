package seng302.Model;

import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class DonorDetails {
  @Expose
  private HashSet<Organs> organs;
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

  public void addOrgan(Organs organ) {
    attachedUser.updateLastModified();
    if (organs == null) {
      organs = new HashSet<>();
    }
    this.organs.add(organ);
  }

  public void removeOrgan(Organs organ) {
    if (organs.contains(organ)) {
      organs.remove(organ);
    }
  }

  /**
   * TODO update if/when more details are added
   * @return true if underlying organs list is empty
   */
  public boolean isEmpty() {
    return organs.isEmpty();
  }
}
