package seng302.Model;

import com.google.gson.annotations.Expose;

import java.util.*;
import java.time.LocalDate;

/**
 * Class for receiver organs
 */
public class ReceiverDetails {

  private transient User attachedUser;
  @Expose
  private Map<Organs, Stack<ReasonAndDateHolderForReceiverDetails>> organs; // contains the organ start and stop dates
  //It's a stack because i was only ever needing to access the last item put in and writing "organs.get(organ).get(organs.get(organ).size() - 1)" was annoying and added heaps of clutter
  /**
   * Constructor without EnumMap; an empty one is generated
   * @param attachedUser user to create the details for
   */
  public ReceiverDetails(User attachedUser) {
    this.attachedUser = attachedUser;
    this.organs = new EnumMap<>(Organs.class);

  }

  /**
   * Constructor with an already populated EnumMap. Not currently used
   * @param attachedUser user to create the details for
   * @param organs EnumMap of organs(k) with dates and reasons(v)
   */
  public ReceiverDetails(User attachedUser, EnumMap<Organs, Stack<ReasonAndDateHolderForReceiverDetails>> organs) {
    this.attachedUser = attachedUser;
    this.organs = organs;
  }
//TODO model from DonorDetails (get/set/add/remove/isEmpty etc) <-["What is this" James]


  /**
   * Gets the map of organs
   * @return EnumMap of organs
   */
  public Map<Organs, Stack<ReasonAndDateHolderForReceiverDetails>> getOrgans() {
    return organs;
  }

  /**
   * Sets the organ EnumMap to a provided EnumMap
   * @param organs a provided organs EnumMap
   */
  public void setOrgans(Map<Organs, Stack<ReasonAndDateHolderForReceiverDetails>> organs) {
    this.organs = organs;
  }

  /**
   * Gets the dates (start and stop) of an organ
   * @param organ organ to get the dates for
   * @return collection of LocalDates
   */
  public Collection<LocalDate> getOrganDates(Organs organ){
    //int size = organs.get(organ).size();
    Collection<LocalDate> list = new ArrayList<LocalDate>();
    if (!organs.get(organ).empty()) {
      Stack<ReasonAndDateHolderForReceiverDetails> tempStack;
      tempStack = organs.get(organ);
      while (!tempStack.empty()) {
        ReasonAndDateHolderForReceiverDetails holder = organs.get(organ).pop();
        list.add(holder.getStartDate());
        list.add(holder.getStopDate());
      }
    }
    return list;
  }

  /**
   * Returns true if the receiver (user) is currently waiting for the particular organ passed in.
   * Checks this by seeing if the start date is not null (receiving has activated) and the stop
   * date is null (has not been stopped)
   * @param organ organ to check receiving status for
   * @return boolean value of whether receiver is still receiving or not
   */
  public boolean isCurrentlyWaitingFor(Organs organ) {
    boolean result = false;

    if (organs.containsKey(organ) && !organs.get(organ).empty()) { //if the specified organ has a key in the map (has been registered before)
      ReasonAndDateHolderForReceiverDetails holder = organs.get(organ).peek();
      if ((holder.getStartDate() != null) && (holder.getStopDate() == null)) {
        result = true; //If at least one of them is started but not stopped
      }
    }
    return result;
  }


  /**
   * appends one organ to the list of organs this user is waiting for, and adds a start date for receiving. If the
   * user is already waiting for this organ, no change will be made.
   * @param organ organ to start waiting for a transplant for (start receiving)
   */
  public void startWaitingForOrgan(Organs organ) {
    if (isCurrentlyWaitingFor(organ)) {
      return; //Do nothing. You need to stop waiting for that organ before your can start again.
    }

    ReasonAndDateHolderForReceiverDetails holder = new ReasonAndDateHolderForReceiverDetails(LocalDate.now(), null, null); //create a new holder for dates and reason

    if (organs.containsKey(organ)) { //This organ has existing map but is not waiting, so needs a new waiting entry
      organs.get(organ).push(holder);

    } else { //create new entry
      organs.put(Organs.values()[organ.ordinal()], new Stack<>()); //create new key value pair for the organ EnumMap
      organs.get(organ).push(holder); //Add the holder to the new Value (ArrayList) in the map

    }
  }

  /**
   * If the user is currently waiting for an organ, adds a timestamp to the list
   *
   * @param organ organ to stop waiting for
   */
  public void stopWaitingForOrgan(Organs organ, OrganDeregisterReason reason) {
    if (isCurrentlyWaitingFor(organ)) {
      organs.get(organ).peek().setStopDate(LocalDate.now()); //If you are waiting for something it should be at the back of the list.
      organs.get(organ).peek().setOrganDeregisterReason(reason);
    } //else nothing (they cannot stop receiving something they are not receiving)
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
   * Uses attachedUser field to determine if the user is already donating the selected organ
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
