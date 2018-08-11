package odms.commons.utils;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import odms.commons.model._enum.Organs;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ProgressTask extends Task<Void> {
    private final Double time;
    private Organs organ;
    private LocalDateTime death;
    private int startTime;
    private ProgressBar bar;

    public ProgressTask(LocalDateTime death, Organs organ) {
        this.organ = organ;
        this.death = death;
        this.time = ((double) death.until(death.plusSeconds((long) organ.getStorageHours()), ChronoUnit.SECONDS));
        this.startTime = (int) death.until(LocalDateTime.now(), ChronoUnit.SECONDS);

    }

    @Override
    protected Void call() throws Exception {
        if (bar == null) {
            return null; // Doesn't start the task without a progress bar
        }
        this.updateProgress(1, 1);

        for (int i = this.startTime; i < time; i++) {
            updateProgress(((time - i) / time), 1);
            bar.setStyle(getColorStyle(((time - i) / time)));
            Thread.sleep(1000);
        }
        this.updateProgress(0, 1);
        return null;
    }

    private String getColorStyle(double progress) {
        // this doesn't work yet =/
        String green;
        String red;
        System.out.println(progress);
        // more red as it is closer to expiring
        green = Integer.toHexString((int) Math.round((progress) * 255));
        if (green.length() == 1) {
            green = "0" + green;
        }
        // more green as you there is more time
        red = Integer.toHexString((int) Math.round((progress) * 255));
        if (red.length() == 1) {
            red = "0" + red;
        }

        String colour = "#" + red + green + "00";

        return "-fx-accent: " + colour;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.bar = progressBar;
    }

}
