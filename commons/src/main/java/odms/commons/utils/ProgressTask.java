package odms.commons.utils;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class ProgressTask extends Task<Void> {
    private final Double time;

    ProgressTask(Double time) {
        this.time = time;
    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
        for (int i = 0; i < time; i++) {
            updateProgress((1.0 * i) / time, 1);
            Thread.sleep(100);
        }
        this.updateProgress(1, 1);
        return null;
    }
}
