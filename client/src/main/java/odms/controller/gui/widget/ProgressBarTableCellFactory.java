package odms.controller.gui.widget;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import odms.commons.utils.ProgressBarService;

public class ProgressBarTableCellFactory {

    public static <T> TableCell<T, ProgressBarService> generateCell(TableColumn<T, ProgressBarService> column) {
        ProgressBar progressBar = new ProgressBar(1.0F);
        TableCell<T, ProgressBarService> cell = new TableCell<T, ProgressBarService>() {
            @Override
            protected void updateItem(ProgressBarService item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    progressBar.progressProperty().bind(item.progressProperty());
                    progressBar.minWidthProperty().bind(column.widthProperty().subtract(10));
                    item.setProgressBar(progressBar);
                    if (!item.isRunning()) {
                        item.restart();
                    }
                }
            }
        };
        cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(progressBar));
        return cell;
    }
}
