package odms.commons.utils;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 * Helper class that deals with progress bars
 */
public class ProgressBarHelper {

    /**
     * Generates a progress bar and binds it to a table view column
     *
     * @param progressBarColumn the column to bind the progress bar to
     * @param <T> the type of the TableView generic type
     * @return the column cell that the progress bar has been bound to
     */
    public static <T> TableCell<T, Service> generateProgressBar(TableColumn<T, Service> progressBarColumn) {
        ProgressBar progressBar = new ProgressBar(1.0F);
        TableCell<T, Service> cell = new TableCell<T, Service>() {
            @Override
            protected void updateItem(Service item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    progressBar.progressProperty().bind(item.progressProperty());
                    progressBar.minWidthProperty().bind(progressBarColumn.widthProperty().subtract(10));
                    progressBar.setStyle("-fx-background-color: -fx-box-border, GREEN; -fx-accent: GREEN; ");

                    if (!item.isRunning()) {
                        item.restart();
                    }
                    if (item.getProgress() < 0.95) {
                        progressBar.setStyle("-fx-background-color: -fx-box-border, RED; -fx-accent: RED; ");
                    }
                }
            }
        };
        cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(progressBar));
        return cell;
    }
}
