package odms.controller.gui.widget;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import odms.commons.model._abstract.Expirable;
import odms.commons.utils.ProgressTask;
import odms.services.CachedThreadPool;

public class ProgressBarTableCellFactory {

    public static <T extends Expirable> TableCell<T, ProgressTask> generateCell(TableColumn<T, ProgressTask> column) {
        ProgressBar progressBar = new ProgressBar(1.0F);
        progressBar.setStyle("-fx-accent: RED; -fx-control-inner-background: rgba(255, 255, 255, 0.1)");
        TableCell<T, ProgressTask> cell = new TableCell<T, ProgressTask>() {
            @Override
            protected void updateItem(ProgressTask item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    progressBar.minWidthProperty().bind(column.widthProperty().subtract(10));
                    if (!item.isExpired()) {
                        progressBar.progressProperty().bind(item.progressProperty());
                        item.setProgressBar(progressBar);
                        CachedThreadPool.getThreadPool().getExecutor().submit(item);
                    }
                }
            }
        };
        cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(progressBar));
        return cell;
    }
}
