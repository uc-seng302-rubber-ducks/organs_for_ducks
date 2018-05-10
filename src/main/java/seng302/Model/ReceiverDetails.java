package seng302.Model;

import com.google.gson.annotations.Expose;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.*;
import java.time.LocalDate;

/**
 * Class for receiver organs
 */
public class ReceiverDetails {

  private transient User attachedUser;
  @Expose
  private Map<Organs, OrganAndDateHolderForReceiverDetails> organs; // contains the organ start and stop dates

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
  public ReceiverDetails(User attachedUser, EnumMap<Organs, OrganAndDateHolderForReceiverDetails> organs) {
    this.attachedUser = attachedUser;
    this.organs = organs;
  }
//TODO model from DonorDetails (get/set/add/remove/isEmpty etc)


  /**
   * Gets the map of organs
   * @return EnumMap of organs
   */
  public Map<Organs, OrganAndDateHolderForReceiverDetails> getOrgans() {
    return organs;
  }

  /**
   * Sets the organ EnumMap to a provided EnumMap
   * @param organs a provided organs EnumMap
   */
  public void setOrgans(Map<Organs, OrganAndDateHolderForReceiverDetails> organs) {
    this.organs = organs;
  }

  /**
   * Gets the dates (start and stop) of an organ
   * @param organ organ to get the dates for
   * @return collection of LocalDates
   */
  public Collection<LocalDate> getOrganDates(Organs organ){
    OrganAndDateHolderForReceiverDetails holder = organs.get(organ);
    Collection<LocalDate> list = new ArrayList<LocalDate>();
    list.add(holder.getStartDate());
    list.add(holder.getStopDate());

    return list;
  }

  //TODO Refactor below code to accomodate changes made by OrganAndDateHolder

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
   */
  public void startWaitingForOrgan(Organs organ) {
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
      organs.put(Organs.values()[organ.ordinal()], list);
    }
  }

  /**
   * if the user is currently waiting for an organ, adds a timestamp to the list
   *
   * @param organ organ to stop waiting for
   */
  public void stopWaitingForOrgan(Organs organ, OrganDeregisterReason reason) {
    if (isCurrentlyWaitingFor(organ)) {
      ArrayList<LocalDate> dates = organs.get(organ);
      dates.add(LocalDate.now());
      organs.put(Organs.values()[organ.ordinal()], dates);
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
