package seng302.Model;

import com.sun.org.apache.xpath.internal.operations.Or;

import java.time.LocalDate;
import java.util.ArrayList;

public class OrganAndDateHolderForReceiverDetails { //Don't @ my naming conventions

    private LocalDate startDate; //Date started receiving
    private LocalDate stopDate; //Date stopped receiving
    private OrganDeregisterReason reason; //Reason the organ was no longer needing to be received

    /**
     * Constructor
     * @param startDate LocalDate date of starting needing to receive
     * @param stopDate LocalDate date of stopping needing to receive
     * @param reason OrganDeregisterReason (enum) of stopping needing to receive
     */
    public OrganAndDateHolderForReceiverDetails(LocalDate startDate, LocalDate stopDate, OrganDeregisterReason reason) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.reason = reason;
    }

    public LocalDate getStartDate() {
        LocalDate date = startDate;
        return date;
    }

    public void setStartDate(LocalDate date) {
        startDate = date;
    }

    public LocalDate getStopDate() {
        LocalDate date = stopDate;
        return date;
    }

    public OrganDeregisterReason getOrganDeregisterReason() {
        OrganDeregisterReason why = reason;
        return why;
    }

    public void setOrganDeregisterReason(OrganDeregisterReason why) {
        reason = why;
    }

}
