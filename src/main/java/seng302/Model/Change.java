package seng302.Model;

import org.joda.time.DateTime;

public class Change {

    private DateTime time;
    private String change;

    public Change(DateTime time, String change) {
        this.time = time;
        this.change = change;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    @Override
    public String toString() {
        return "Change{" +
                "time=" + time +
                ", change='" + change + '\'' +
                '}';
    }
}
