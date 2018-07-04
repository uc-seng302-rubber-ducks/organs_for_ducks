package seng302.model.datamodel;

import java.time.LocalDateTime;

/**
 * for use with the timed entry cache
 *
 * @param <T> type of the value to be stored
 * @see seng302.model._abstract.TimedEntryCache
 */
public class TimedCacheValue<T> {
    private LocalDateTime dateTime;
    private T value;

    public TimedCacheValue(T value) {
        this.value = value;
        this.dateTime = LocalDateTime.now();
    }

    public TimedCacheValue() {
        this.value = null;
        this.dateTime = LocalDateTime.now();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LocalDateTime getDateTime() {

        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
