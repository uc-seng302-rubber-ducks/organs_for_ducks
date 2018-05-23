package seng302.Model;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
@Deprecated
public class ReasonAndDateHolderForReceiverDetails { //Don't @ my naming conventions.
    //More seriously, we are starting to rack up classes and the naming for them needs to be more specific.
    // This might be over-doing it but wanted to start a discussion

    private LocalDate startDate = null; //Date started receiving
    private LocalDate stopDate = null; //Date stopped receiving
    private OrganDeregisterReason reason = null; //Reason the organ was no longer needing to be received

    /**
     * Constructor
     * @param startDate LocalDate date of starting needing to receive
     * @param stopDate LocalDate date of stopping needing to receive
     * @param reason OrganDeregisterReason (enum) of stopping needing to receive
     */
    public ReasonAndDateHolderForReceiverDetails(LocalDate startDate, LocalDate stopDate, OrganDeregisterReason reason) {
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

    public void setStopDate(LocalDate date) {
        stopDate = date;
    }

    public OrganDeregisterReason getOrganDeregisterReason() {
        OrganDeregisterReason why = reason;
        return why;
    }

    public void setOrganDeregisterReason(OrganDeregisterReason why) {
        reason = why;
    }

    @Override
    public String toString() {
        String start = "{\n    Start date: " + startDate.toString() + "\n";
        String stop = "    End Date: " + stopDate.toString() + "\n";
        String stringReason = "    Reason: ";
        if (reason != null) {
            stringReason += reason.toString() + "\n";
        } else {
            stringReason += "N/A\n}";
        }

        return (start + stop + stringReason);
    }

}
