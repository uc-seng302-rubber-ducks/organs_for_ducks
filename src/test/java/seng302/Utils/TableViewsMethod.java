package seng302.Utils;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import org.loadui.testfx.controls.TableViews;
import org.loadui.testfx.exceptions.NoNodesFoundException;

import static org.loadui.testfx.GuiTest.find;

/**
 * contains methods that manipulates javaFX TableView elements
 * for testing purposes.
 *
 * @author acb116
 */
public class TableViewsMethod extends TableViews{

    /**
     * gets the cell from desired table
     * @param tableName of desired table
     * @param row number
     * @param column number
     * @return table cell
     */
    public static javafx.scene.control.TableCell<?,?> getCell(String tableName, int row, int column) {
        return cell(tableName, row, column);
    }

    /**
     * gets content of a cell
     * from desired table
     * @param tableName of desired table
     * @param column number
     * @param row number
     * @return the content of a cell
     */
    public static Object getCellValue(String tableName, int column, int row) {
        TableView<?> table = getTableView(tableName);
        return table.getColumns().get(column).getCellObservableValue(row).getValue();
    }

    /**
     * gets tableView object based on
     * table name
     * @param tableName of desired table
     * @return tableView object
     */
    private static TableView<?> getTableView(String tableName) {
     Node node = find(tableName);
     if (!(node instanceof TableView)) {
         throw new NoNodesFoundException(tableName + " selected " + node + " which is not a TableView!");
     }
    return (TableView<?>) node;
    }
}
