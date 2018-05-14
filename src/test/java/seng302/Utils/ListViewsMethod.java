package seng302.Utils;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import org.loadui.testfx.controls.TableViews;
import org.loadui.testfx.exceptions.NoNodesFoundException;

import static org.loadui.testfx.GuiTest.find;

/**
 * contains methods that manipulates javaFX ListView elements
 * for testing purposes.
 *
 * @author acb116 jre103
 */
public class ListViewsMethod extends TableViews{
    /**
     * gets content of a cell
     * from desired ListView
     * @param listViewName of desired ListView
     * @param row number
     * @return the content of a row
     */
    public static Object getRowValue(String listViewName, int row) {
        ListView<?> listView = getListView(listViewName);
        listView.getSelectionModel().select(row);
        return listView.getSelectionModel().getSelectedItem();
    }

    /**
     * gets ListView object based on
     * ListView name
     * @param ListName of desired ListView
     * @return tableView object
     */
    private static ListView<?> getListView(String ListName) {
     Node node = find(ListName);
     if (!(node instanceof ListView)) {
         throw new NoNodesFoundException(ListName + " selected " + node + " which is not a ListView!");
     }
    return (ListView<?>) node;
    }
}
