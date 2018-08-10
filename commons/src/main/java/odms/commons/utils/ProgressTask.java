package odms.commons.utils;

import javafx.concurrent.Task;

public class ProgressTask extends Task<Void> {
    private final Double time;

    public ProgressTask(Double time) {
        this.time = time;
    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(1, 1);

        for (int i = 0; i < time; i++) {
            updateProgress(time-i, time);
            Thread.sleep(1000);
        }
        this.updateProgress(0, 1);
        return null;
    }
}
