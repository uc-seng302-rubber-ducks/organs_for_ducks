package seng302.Model;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Class to do all the heavy lifting on durations and pairing up dates for starting and stopping medications
 * Also allows the medication listView to be populated
 * Duration is in days
 *
 * @author Josh Burt
 */
public class MedicationDurations {

    private LocalDateTime start;
    private LocalDateTime stop;
    private int duration;

    /**
     * Constructor for Medication Durations
     * @param start start date/time
     * @param stop stop date/time
     */
    public MedicationDurations(LocalDateTime start, LocalDateTime stop) {
        this.start = start;
        this.stop = stop;
        duration = (int) ChronoUnit.DAYS.between(start, stop);
    }

    /**
     * Constructor for Medication Durations
     * @param start start date/time
     */
    public MedicationDurations(LocalDateTime start) {
        this.start = start;
        duration = 0;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
        if(stop != null){
            duration = (int) ChronoUnit.DAYS.between(start,stop);
        } else {
            duration = 0;
        }
    }

    public LocalDateTime getStop() {
        return stop;
    }

    public void setStop(LocalDateTime stop) {
        this.stop = stop;
        duration = (int) ChronoUnit.DAYS.between(start,stop);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
