package odms.commons.utils;


import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;

public class OrganListCellFactory {

    private OrganListCellFactory() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * Generates a TableCell object and that recolours text based on certain fields
     *
     * @param column      The table column to be which holds an organ type to be highlighted
     * @param currentUser The current user
     * @param <T>         Generic type of the table column
     * @return A new TableCell object
     */
    public static <T> TableCell<T, Organs> generateOrganTableCell(TableColumn<T, Organs> column, User currentUser) {
        return new TableCell<T, Organs>() {
            @Override
            public void updateItem(final Organs item, final boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    if (currentUser.getCommonOrgans().contains(item)) {
                        this.setTextFill(Color.RED);
                    } else {
                        this.setTextFill(Color.BLACK);
                    }
                    setText(item.toString());
                }
            }
        };
    }


    /**
     * Generates a ListCell object that recolours text based on certain fields
     *
     * @param user The current user
     * @return A new ListCell object
     */
    public static ListCell<Organs> generateListCell(User user) {
        return (new ListCell<Organs>() {
            @Override
            protected void updateItem(Organs item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    return;
                }

                if (user.getCommonOrgans().contains(item)) {
                    this.setTextFill(Color.RED);
                } else {
                    this.setTextFill(Color.BLACK);
                }
                setText(item.toString());
            }
        });
    }
}