package odms.commons.utils;

import javafx.concurrent.Task;
import odms.commons.model._enum.Organs;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ProgressTask extends Task<Void> {
    private final Double time;
    private String colorStyle;
    private Organs organ;
    private LocalDateTime death;
    private int startTime;

    public ProgressTask(LocalDateTime death, Organs organ) {
        this.organ = organ;
        this.death = death;
        this.time = ((double) death.until(death.plusSeconds((long) organ.getStorageHours()), ChronoUnit.SECONDS));
        this.startTime = (int) death.until(LocalDateTime.now(), ChronoUnit.SECONDS);

    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(1, 1);

        for (int i = this.startTime; i < time; i++) {
            updateProgress(((time - i) / time), 1);

            Thread.sleep(1000);
        }
        this.updateProgress(0, 1);
        return null;
    }

    public String getColorStyle() {
        return colorStyle;
    }

    public void setColorStyle(String colorStyle) {
        this.colorStyle = colorStyle;
    }

}
