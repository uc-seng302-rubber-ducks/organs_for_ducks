package odms.controller.gui.panel.view;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import odms.commons.model._enum.AppointmentStatus;

public class AppointmentTableCellFactory {

    private AppointmentTableCellFactory() {
        throw new IllegalStateException("Utility Class");
    }

    public static <T> TableCell<T, AppointmentStatus> generateAppointmentTableCell() { //TableColumn<T, Appointment> column
        return new TableCell<T, AppointmentStatus>() {
            @Override
            public void updateItem(final AppointmentStatus item, final boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    if (this.getItem().equals(AppointmentStatus.REJECTED)
                            || this.getItem().equals(AppointmentStatus.REJECTED_SEEN)
                            || this.getItem().equals(AppointmentStatus.CANCELLED_BY_CLINICIAN)
                            || this.getItem().equals(AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN)
                            || this.getItem().equals(AppointmentStatus.CANCELLED_BY_USER)
                            || this.getItem().equals(AppointmentStatus.CANCELLED_BY_USER_SEEN)) {
                        this.setTextFill(Color.RED);
                    } else if (this.getItem().equals(AppointmentStatus.ACCEPTED)
                            || this.getItem().equals(AppointmentStatus.ACCEPTED_SEEN)) {
                        this.setTextFill(Color.GREEN);
                    } else if (this.getItem().equals(AppointmentStatus.PENDING)) {
                        this.setTextFill(Color.GOLDENROD);
                    } else {
                        this.setTextFill(Color.BLACK); //This only occurs if the status is updated. 
                    }
                    setText(item.toString());

                }
            }
        };
    }
}
