package odms.controller.gui.widget;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

/**
 * An extension of a table view to allow the capability of showing when it is loading data.
 */
public class CountableLoadingTableView<T> extends TableView<T> implements CountableLoadingWidget {
    private IntegerProperty count;

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

    /**
     * A observable property that holds the count of the objects that will be in the table
     * @return the count property
     */
    public IntegerProperty countProperty() {
        if (count == null) {
            count = new SimpleIntegerProperty(0);
        }
        return count;
    }

    @Override
    public void setCount(int value) {
        countProperty().setValue(value);
    }

    @Override
    public int getCount() {
        return countProperty().get();
    }
}
