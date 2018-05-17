package seng302.Controller;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.json.simple.JSONObject;
import seng302.Model.OrganDeregisterReason;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReceiverOrganDetailsHolder {

    private LocalDate startDate = null; //Date started receiving
    private LocalDate stopDate = null; //Date stopped receiving
    private OrganDeregisterReason reason = null; //Reason the organ was no longer needing to be received

    /**
     * Constructor
     * @param startDate LocalDate date of starting needing to receive
     * @param stopDate LocalDate date of stopping needing to receive
     * @param reason OrganDeregisterReason (enum) of stopping needing to receive
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
        this.startDate = date;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDate date) {
        this.stopDate = date;
    }

    public OrganDeregisterReason getOrganDeregisterReason() {
        return reason;
    }

    public void setOrganDeregisterReason(OrganDeregisterReason reason) {
        this.reason = reason;
    }

    /**
     * Thought I needed this when I wrote it, but I don't. Useful for debugging, otherwise delete
     * @return string to print
     */
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
