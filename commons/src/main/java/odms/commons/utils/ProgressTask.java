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
    private double lowerBound = 0.0;
    private double colourPercent = 0.0;

    public ProgressTask(LocalDateTime death, Organs organ) {
        this.organ = organ;
        this.death = death;
        this.time = ((double) death.until(death.plusSeconds(organ.getUpperBoundSeconds()), ChronoUnit.SECONDS));
        this.startTime = (int) death.until(LocalDateTime.now(), ChronoUnit.SECONDS);
        if (organ.getUpperBoundSeconds() != organ.getLowerBoundSeconds()) {
            this.lowerBound = 1.0 - (organ.getLowerBoundSeconds() / organ.getUpperBoundSeconds());
            colourPercent = Math.round(this.lowerBound * 100);
        }


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
            // remove all green color from the  back ground
        if (progress <= (this.lowerBound)) {
                // replace this when organs have a lower bound
                colour = "#" + red + "00" + "00";
            }
        return "-fx-accent: " + colour + "; -fx-control-inner-background: rgba(255, 255, 255, 0.1);  -fx-background-color: linear-gradient(to left, Maroon , Maroon " + colourPercent + "% , transparent " + colourPercent + "%); ";
        }


    private String getTimeRemaining() {
        int hours = (int) HOURS.between(LocalDateTime.now(), death.plusSeconds(organ.getUpperBoundSeconds()));
        int mins = (int) MINUTES.between(LocalDateTime.now(), death.plusSeconds(organ.getUpperBoundSeconds())) - hours * 60;
        int seconds = (int) SECONDS.between(LocalDateTime.now(), death.plusSeconds(organ.getUpperBoundSeconds())) - hours * 3600 - mins * 60;
        return String.format("%d h %d m %d s remaining", hours, mins, seconds);
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.bar = progressBar;
    }

}