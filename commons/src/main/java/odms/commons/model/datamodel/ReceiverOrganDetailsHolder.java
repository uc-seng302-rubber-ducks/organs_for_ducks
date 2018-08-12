package odms.commons.model.datamodel;

import odms.commons.model._enum.OrganDeregisterReason;

import java.time.LocalDate;

public class ReceiverOrganDetailsHolder {

    private LocalDate startDate = null; //Date started receiving
    private LocalDate stopDate = null; //Date stopped receiving
    private OrganDeregisterReason reason = null; //Reason the organ was no longer needing to be received

    /**
     * Empty constructor
     */
    public ReceiverOrganDetailsHolder() {
    }

    /**
     * Constructor
     *
     * @param startDate LocalDate date of starting needing to receive
     * @param stopDate  LocalDate date of stopping needing to receive
     * @param reason    OrganDeregisterReason (enum) of stopping needing to receive
     */
    public ReceiverOrganDetailsHolder(LocalDate startDate, LocalDate stopDate, OrganDeregisterReason reason) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.reason = reason;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate date) {
        startDate = date;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDate date) {
        stopDate = date;
    }

    public OrganDeregisterReason getOrganDeregisterReason() {
        return reason;
    }

    public void setOrganDeregisterReason(OrganDeregisterReason why) {
        reason = why;
    }

    @Override
    public String toString() {
        return "ReceiverOrganDetails{" +
                "RegisterDate: " + startDate +
                "Deregister date: " + stopDate +
                "Deregister Reason: " + reason;
    }

}
