package seng302.Model;

import com.google.gson.annotations.Expose;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Class for receiver organs
 */
public class ReceiverDetails {


  private transient User attachedUser;

  @Expose
  private Map<Organs, ArrayList<LocalDate>> organs; // contains the organ start and stop dates

  public ReceiverDetails(User attachedUser) {
    this.attachedUser = attachedUser;
    this.organs = new EnumMap<>(Organs.class);

  }

  public ReceiverDetails(User attachedUser, EnumMap<Organs, ArrayList<LocalDate>> organs) {
    this.attachedUser = attachedUser;
    this.organs = organs;
  }
//TODO model from DonorDetails (get/set/add/remove/isEmpty etc)


  public Map<Organs, ArrayList<LocalDate>> getOrgans() {
    return organs;
  }

  public void setOrgans(
      Map<Organs, ArrayList<LocalDate>> organs) {
    this.organs = organs;
  }

  public List<LocalDate> getOrganDates(Organs organ){
    return organs.get(organ);
  }

  /**
   * determines whether a user is waiting for the given organ
   * organ is in list and uneven number of time entries.
   * time entries can be grouped in pairs of start/stop times. uneven would mean they are currently waiting
   * @param organ organ in question
   * @return true if organ is being waited for
   */
  public boolean isCurrentlyWaitingFor(Organs organ) {
    return organs != null && organs.containsKey(organ) && organs.get(organ).size() % 2 == 1;
  }

  /**
   * appends one organ to the list of organs this user is waiting for. If the user is already
   * waiting for this organ, no change will be made.
   * @param organ
   * @return true if the collection was modified
   */
  public boolean startWaitingForOrgan(Organs organ) {
    if (isCurrentlyWaitingFor(organ)) {
      return false;
    }
    Memento<User> memento = new Memento<>();
    memento.setOldObject(attachedUser.clone());
    //existing entry
    if (organs.containsKey(organ)) {
      organs.get(organ).add(LocalDate.now());
    } else {
      //create new entry
      ArrayList<LocalDate> list = new ArrayList<>();
      list.add(LocalDate.now());
      organs.put(Organs.values()[organ.ordinal()], list);
    }
    memento.setNewObject(attachedUser.clone());
    attachedUser.getUndoStack().push(memento);
    attachedUser.getRedoStack().clear();
    return true;
  }

  /**
   * if the user is currently waiting for an organ, adds a timestamp to the list
   *
   * @param organ organ to stop waiting for
   * @return true if the collection was modified.
   */
  public boolean stopWaitingForOrgan(Organs organ) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(attachedUser.clone());
    if (isCurrentlyWaitingFor(organ)) {
      ArrayList<LocalDate> dates = organs.get(organ);
      dates.add(LocalDate.now());
      organs.put(Organs.values()[organ.ordinal()], dates);
      return true;
    }
    memento.setNewObject(attachedUser.clone());
    attachedUser.getUndoStack().push(memento);
    attachedUser.getRedoStack().clear();
    return false;
  }

  /**
   * Stop waiting for all organs for a receiver.
   */
  public void stopWaitingForAllOrgans(){
    Map<Organs, ArrayList<LocalDate>> organsCopy = new EnumMap<>(organs);
    for(Organs organ: organsCopy.keySet()){
      stopWaitingForOrgan(organ);
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


  /**
   * creates a simple space separated string of the organs this user is currently waiting to
   * receive
   *
   * @return String of format "organ1 organ2 organ3 "
   */
  public String stringIsWaitingFor() {
    StringBuilder sb = new StringBuilder();
    for (Organs o : organs.keySet()) {
      if (isCurrentlyWaitingFor(o)) {
        sb.append(o.organName);
        sb.append(" ");
      }
    }
    return sb.toString();
  }
}
