package seng302.Model;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Class to do all the heavy lifting on durations and pairing up dates for starting and stopping medications
 * Also allows the medication listView to be populated
 * Duration is in days
 *
 * @author Josh Burt
 */
public class MedicationDurations {

    private DateTime start;
    private DateTime stop;
    private int duration;

    public MedicationDurations(DateTime start, DateTime stop) {
        this.start = start;
        this.stop = stop;
        duration = Days.daysBetween(start.toInstant(), stop.toInstant()).getDays();
    }

    public MedicationDurations(DateTime start) {
        this.start = start;
        duration = 0;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
        if(stop != null){
            duration = Days.daysBetween(start.toInstant(),stop.toInstant()).getDays();
        } else {
            duration = 0;
        }
    }

    public DateTime getStop() {
        return stop;
    }

    public void setStop(DateTime stop) {
        this.stop = stop;
        duration = Days.daysBetween(start.toInstant(),stop.toInstant()).getDays();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
