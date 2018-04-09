package seng302.Model;

import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class ReceiverDetails {
  @Expose
  private HashSet<Organs> organs;
  private transient User attachedUser;

  public ReceiverDetails (User attachedUser){
    this.attachedUser = attachedUser;
  }

  public HashSet<Organs> getOrgans() {
    return organs;
  }

  public void setOrgans(HashSet<Organs> organs) {
    attachedUser.updateLastModified();
    this.organs = organs;
  }
  //TODO model from DonorDetails (get/set/add/remove/isEmpty etc)

}
