package odms.controller.gui.widget;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.utils.ProgressTask;
import odms.services.CachedThreadPool;

public class ProgressBarTableCellFactory {

    public static TableCell<AvailableOrganDetail, ProgressTask> generateCell(TableColumn<AvailableOrganDetail, ProgressTask> column) {
        ProgressBar progressBar = new ProgressBar(1.0F);
        TableCell<AvailableOrganDetail, ProgressTask> cell = new TableCell<AvailableOrganDetail, ProgressTask>() {
            @Override
            protected void updateItem(ProgressTask item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    progressBar.progressProperty().bind(item.progressProperty());
                    progressBar.minWidthProperty().bind(column.widthProperty().subtract(10));
                    item.setProgressBar(progressBar);
                    CachedThreadPool.getThreadPool().getExecutor().submit(item);
                }
            }
        };
        cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(progressBar));
        return cell;
    }
}
