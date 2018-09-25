package odms.controller.gui.widget;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

/**
 * An extension of a table view to allow the capability of showing when it is loading data.
 */
public class LoadingTableView<T> extends TableView<T> implements LoadingWidget {
    /**
     * Sets the placeholder of the tableview by telling it whether or not it is waiting for incoming data
     */
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
