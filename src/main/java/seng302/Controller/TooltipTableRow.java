package seng302.Controller;
import java.util.function.Function;

import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;

public class TooltipTableRow<T> extends TableRow<T> {

    private Function<T, String> toolTipStringFunction;

    /**
     *
     * @param toolTipStringFunction A tooltip function.
     */
    public TooltipTableRow(Function<T, String> toolTipStringFunction) {
        this.toolTipStringFunction = toolTipStringFunction;
    }

    /**
     *
     * @param item T item.
     * @param empty boolean if empty or not.
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if(item == null) {
            setTooltip(null);
        } else {
            Tooltip tooltip = new Tooltip(toolTipStringFunction.apply(item));
            setTooltip(tooltip);
        }
    }
}
