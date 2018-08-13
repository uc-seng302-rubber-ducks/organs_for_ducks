package odms.commons.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import odms.commons.model._enum.Organs;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.*;

public class ProgressTask extends Task<Void> {
    private final Double time;
    private Organs organ;
    private LocalDateTime death;
    private int startTime;
    private ProgressBar bar;

    public ProgressTask(LocalDateTime death, Organs organ) {
        this.organ = organ;
        this.death = death;
        this.time = ((double) death.until(death.plusSeconds(organ.getStorageSeconds()), ChronoUnit.SECONDS));
        this.startTime = (int) death.until(LocalDateTime.now(), ChronoUnit.SECONDS);

    }

    @Override
    protected Void call() throws Exception {
        if (bar == null) {
            return null; // Doesn't start the task without a progress bar
        }
        this.updateProgress(0, 1);

        for (int i = this.startTime; i < time; i++) {
            updateProgress(((i) / time), 1);
            updateMessage(getTimeRemaining());
            final int a = i;
            Platform.runLater(() -> bar.setStyle(getColorStyle(((time - a) / time))));
            Thread.sleep(1000);
        }
        this.updateProgress(1, 1);
        return null;
    }

    private String getColorStyle(double progress) {
        // this doesn't work yet =/
        String green;
        String red;
        int colourNum;
        if (progress < 0.5) {
            colourNum = (int) Math.round(((progress * 2) * 255));
            green = Integer.toHexString(colourNum);
            if (green.length() == 1) {
                green = "0" + green;
            }

            red = "ff";
        } else {
            colourNum = (int) Math.round(((1 - progress) * 2) * 255);
            red = Integer.toHexString(colourNum);
            if (red.length() == 1) {
                red = "0" + red;
            }
            green = "ff";
        }


        String colour = "#" + red + green + "00";

        return "-fx-accent: " + colour;
    }

    private String getTimeRemaining() {
        int hours = (int) HOURS.between(LocalDateTime.now(), death.plusSeconds(organ.getStorageSeconds()));
        int mins = (int) MINUTES.between(LocalDateTime.now(), death.plusSeconds(organ.getStorageSeconds())) - hours * 60;
        int seconds = (int) SECONDS.between(LocalDateTime.now(), death.plusSeconds(organ.getStorageSeconds())) - hours * 3600 - mins * 60;
        return String.format("%d h %d m %d s remaining", hours, mins, seconds);
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.bar = progressBar;
    }

}