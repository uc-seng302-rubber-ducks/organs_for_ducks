package odms.controller.gui.widget;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

public class LoadingTableView<T> extends TableView<T> {
    public void setWaiting(boolean waiting) {
        if (waiting) {
            ProgressIndicator indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
            indicator.maxHeightProperty().bind(heightProperty().divide(10));
            indicator.maxWidthProperty().bind(widthProperty().divide(10));
            setPlaceholder(indicator);
        } else {
            if (getItems().isEmpty()) {
                setPlaceholder(new Label("There are no results"));
            }
        }
    }
}
