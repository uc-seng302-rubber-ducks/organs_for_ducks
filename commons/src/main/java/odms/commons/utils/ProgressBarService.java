package odms.commons.utils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import odms.commons.model._enum.Organs;

import java.time.LocalDateTime;

public class ProgressBarService extends Service {

    private ProgressTask task;

    public ProgressBarService(LocalDateTime momentOfDeath, Organs organ) {
        super();
        this.task = new ProgressTask(momentOfDeath, organ);

    }

    @Override
    protected Task createTask() {
        return task;
    }

    public void setProgressBar(ProgressBar bar) {
        task.setProgressBar(bar);
    }


}
