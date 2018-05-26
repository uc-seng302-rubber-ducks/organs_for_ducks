package seng302.Model;

import com.google.gson.annotations.Expose;
import seng302.Controller.ReceiverOrganDetailsHolder;

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
    private Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> organs; // contains the organ start and stop dates

    /**
     * Constructor without EnumMap; an empty one is generated
     *
     * @param attachedUser user to create the details for
     */
    ReceiverDetails(User attachedUser) {
        this.attachedUser = attachedUser;
        this.organs = new EnumMap<>(Organs.class);

    }

    /**
     * Gets the map of organs
     *
     * @return EnumMap of organs
     */
    public Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> getOrgans() {
        return organs;
    }


    /**
     * Sets the organ EnumMap to a provided EnumMap
     *
     * @param organs a provided organs EnumMap
     */
    public void setOrgans(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> organs) {
        this.organs = organs;
    }


    public List<LocalDate> getOrganDates(Organs organ) {
        ArrayList<ReceiverOrganDetailsHolder> holderList = organs.get(organ);
        ArrayList<LocalDate> dates = new ArrayList<>();
        for (ReceiverOrganDetailsHolder aHolderList : holderList) {
            dates.add(aHolderList.getStartDate());
            LocalDate stopDate = aHolderList.getStopDate();
            if (stopDate != null) {
                dates.add(stopDate);
            }
        }
        return dates;
    }

    /**
     * Determines whether a user is waiting for the given organ
     * organ is in list and uneven number of time entries.
     * time entries can be grouped in pairs of start/stop times. uneven would mean they are currently waiting
     *
     * @param organ organ in question
     * @return true if organ is being waited for
     */
    public boolean isCurrentlyWaitingFor(Organs organ) {
        boolean result = false;

        if (organs.containsKey(organ) && organs.get(organ).size() != 0) { //if the specified organ has a key in the map (has been registered before)
            ReceiverOrganDetailsHolder holder = organs.get(organ).get(organs.get(organ).size() - 1);
            if ((holder.getStartDate() != null) && (holder.getStopDate() == null)) {
                result = true; //If at least one of them is started but not stopped
            }
        }
        return result;
    }

    /**
     * appends one organ to the list of organs this user is waiting for. If the user is already
     * waiting for this organ, no change will be made.
     *
     * @param organ organ to be waiting for
     * @return true if the collection was modified
     */
    public boolean startWaitingForOrgan(Organs organ) {
        if (isCurrentlyWaitingFor(organ)) {
            return false;
        }
        Memento<User> memento = new Memento<>();
        memento.setOldObject(attachedUser.clone());
        //existing entry

        ReceiverOrganDetailsHolder holder = new ReceiverOrganDetailsHolder(LocalDate.now(), null, null); //create a new holder for dates and reason

        if (organs.containsKey(organ)) { //This organ has existing map but is not waiting, so needs a new waiting entry
            organs.get(organ).add(holder);

        } else { //create new entry
            organs.put(Organs.values()[organ.ordinal()], new ArrayList<>()); //create new key value pair for the organ EnumMap
            organs.get(organ).add(holder); //Add the holder to the new Value (ArrayList) in the map

        }

        memento.setNewObject(attachedUser.clone());
        attachedUser.getUndoStack().push(memento);
        attachedUser.getRedoStack().clear();
        attachedUser.addChange(new Change("Started waiting for a " + organ));
        return true;
    }

    /**
     * if the user is currently waiting for an organ, adds a timestamp and reason to the list
     *
     * @param organ organ to stop waiting for
     * @return true if the collection was modified.
     */
    public boolean stopWaitingForOrgan(Organs organ, OrganDeregisterReason reason) {
        Memento<User> memento = new Memento<>();
        memento.setOldObject(attachedUser.clone());

        if (isCurrentlyWaitingFor(organ)) {
            organs.get(organ).get(organs.get(organ).size() - 1).setStopDate(LocalDate.now()); //If you are waiting for something it should be at the back of the list.
            organs.get(organ).get(organs.get(organ).size() - 1).setOrganDeregisterReason(reason);
        }

        memento.setNewObject(attachedUser.clone());
        attachedUser.getUndoStack().push(memento);
        attachedUser.getRedoStack().clear();
        attachedUser.addChange(new Change("Stopped waiting for a " + organ));
        return true;
    }

    /**
     * if the user is currently waiting for an organ, adds a timestamp and reason to the list
     * No memento actions so that multiple copies aren't created when mass removing upon receiver death
     *
     * @param organ  organ to stop waiting for
     * @param reason true if the collection was modified.
     */
    private void stopWaitingForOrganNoMemento(Organs organ, OrganDeregisterReason reason) {
        if (isCurrentlyWaitingFor(organ)) {
            organs.get(organ).get(organs.get(organ).size() - 1).setStopDate(LocalDate.now()); //If you are waiting for something it should be at the back of the list.
            organs.get(organ).get(organs.get(organ).size() - 1).setOrganDeregisterReason(reason);
        }
    }

    /**
     * Stop waiting for all organs for a receiver.
     */
    public void stopWaitingForAllOrgans() {
        Memento<User> memento = new Memento<>();
        memento.setOldObject(attachedUser.getUndoStack().pop().getOldObject());

        Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> organsCopy = new EnumMap<>(organs);
        for (Organs organ : organsCopy.keySet()) {
            stopWaitingForOrganNoMemento(organ, OrganDeregisterReason.RECEIVER_DIED);
        }


        memento.setNewObject(attachedUser.clone());
        attachedUser.getUndoStack().push(memento);
        attachedUser.getRedoStack().clear();
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
     * check if underlying organs list is empty
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
