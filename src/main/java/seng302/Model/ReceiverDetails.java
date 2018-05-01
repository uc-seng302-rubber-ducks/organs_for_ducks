package seng302.Model;

import com.google.gson.annotations.Expose;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReceiverDetails {


  private transient User attachedUser;

  @Expose
  private HashMap<String, ArrayList<LocalDate>> organs; // contains the organ start and stop dates

  public ReceiverDetails(User attachedUser) {
    this.attachedUser = attachedUser;
    this.organs = new HashMap<>();
  }

  public ReceiverDetails(User attachedUser, HashMap<String, ArrayList<LocalDate>> organs) {
    this.attachedUser = attachedUser;
    this.organs = organs;
  }
//TODO model from DonorDetails (get/set/add/remove/isEmpty etc)


  public HashMap<String, ArrayList<LocalDate>> getOrgans() {
    return organs;
  }

  public List<LocalDate> getOrganDates(Organs organ){
    return organs.get(organ.toString().toUpperCase());
  }

  /**
   * determines whether a user is waiting for the given organ
   * organ is in list and uneven number of time entries.
   * time entries can be grouped in pairs of start/stop times. uneven would mean they are currently waiting
   * @param organ organ in question
   * @return true if organ is being waited for
   */
  public boolean isCurrentlyWaitingFor(String organ) {

    return organs.containsKey(organ) && organs.get(organ).size() % 2 == 1;
  }

  /**
   * appends one organ to the list of organs this user is waiting for. If the user is already
   * waiting for this organ, no change will be made.
   * @param organ
   */
  public void startWaitingForOrgan(String organ) {
    if (isCurrentlyWaitingFor(organ)) {
      return;
    }
    //existing entry
    if (organs.containsKey(organ)) {
      organs.get(organ).add(LocalDate.now());
    } else {
      //create new entry
      ArrayList<LocalDate> list = new ArrayList<>();
      list.add(LocalDate.now());
      organs.put(organ, list);
    }
  }

  /**
   * if the user is currently waiting for an organ, adds a timestamp to the list
   *
   * @param organ organ to stop waiting for
   */
  public void stopWaitingForOrgan(String organ) {
    if (isCurrentlyWaitingFor(organ)) {
      organs.get(organ).add(LocalDate.now());
    }
  }

  public User getAttachedUser() {
    return attachedUser;
  }

  /**
   * USE SPARINGLY. this can easily create consistency issues. Only sensible use case is
   * user.getReceiverDetails().setAttachedUser(user)
   *
   * @param attachedUser user to connect
   */
  public void setAttachedUser(User attachedUser) {
    this.attachedUser = attachedUser;
  }

  /**
   * uses attachedUser field to determine if the user is already donating the selected organ
   *
   * @param organ organ to be tested for
   * @return true if the user's organs to donate contains the given organ
   */
  public boolean isDonatingThisOrgan(Organs organ) {
    return attachedUser.getDonorDetails().getOrgans().contains(organ);
  }

  /**
   * check if underlying organs list is empty TODO extend this to new functionality when added
   *
   * @return true if organ list is empty
   */
  public boolean isEmpty() {
    return organs == null || organs.isEmpty();
  }

}
