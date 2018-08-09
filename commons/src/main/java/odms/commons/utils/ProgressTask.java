package odms.commons.utils;

import javafx.concurrent.Task;
import odms.commons.model.datamodel.AvailableOrganDetail;

public class ProgressTask extends Task<Void> {
    private final Double time;
    private AvailableOrganDetail parent;

    public ProgressTask(Double time, AvailableOrganDetail parent) {
        this.time = time;
        this.parent = parent;
    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(1, 1);
        for (int i = 0; i < time; i++) {
            parent.setProgress((1.0 * (time - i)) / time);
            updateProgress((1.0 * (time - i)) / time, 1);
            Thread.sleep(1000);
        }
        this.updateProgress(0, 1);
        return null;
    }
}
