package odms.commons.utils;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import odms.commons.model._enum.AppointmentStatus;

public class AppointmentTableCellFactory {

    private AppointmentTableCellFactory() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * Colours the appointment status text within the appointment tables according to their status type
     *
     * @param <T>   The generic type of the TableView
     * @return      The table cell with coloured text
     */
    public static <T> TableCell<T, AppointmentStatus> generateAppointmentTableCell() {
        return new TableCell<T, AppointmentStatus>() {
            @Override
            public void updateItem(final AppointmentStatus item, final boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    this.getStyleClass().clear();
                    if (this.getItem().equals(AppointmentStatus.UPDATED)) {
                        this.setTextFill(Color.BLUE);
                    } else if (this.getItem().equals(AppointmentStatus.ACCEPTED)
                            || this.getItem().equals(AppointmentStatus.ACCEPTED_SEEN)) {
                        this.setTextFill(Color.LIMEGREEN);
                    } else if (this.getItem().equals(AppointmentStatus.PENDING)) {
                        this.setTextFill(Color.GOLDENROD);
                    } else {
                        this.setTextFill(Color.RED);
                    }
                    setText(item.toString());
                }
            }
        };
    }
}
