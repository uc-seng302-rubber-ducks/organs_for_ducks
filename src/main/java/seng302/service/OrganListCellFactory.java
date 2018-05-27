package seng302.Service;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import seng302.Model.Organs;
import seng302.Model.User;

public class OrganListCellFactory {

    /**
     * Generates a ListCell object and that recolours text based on certain fields
     *
     * @return a new ListCell object
     */
    public static ListCell<Organs> generateListCell(User user) {
        return (new ListCell<Organs>() {
            @Override
            protected void updateItem(Organs item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : getItem().toString());
                setGraphic(null);

                if (item == null) {
                    return;
                }

                if (user.getCommonOrgans().contains(item)) {
                    setTextFill(Color.RED);
                } else {
                    setTextFill(Color.BLACK);
                }
            }
        });
    }
}
