package seng302.Model;

import org.joda.time.DateTime;

import java.time.LocalDateTime;

public class Change {

    private LocalDateTime time;
    private String change;

    public Change(LocalDateTime time, String change) {
        this.time = time;
        this.change = change;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
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
