package odms.controller.gui.widget;

import com.calendarfx.model.Entry;
import odms.commons.model.Appointment;

/**
 * Factory class to generate entries based on an appointment for a CalendarFX CalendarView
 */
public class CalendarEntryFactory {
    private CalendarEntryFactory() {

    }

    public static Entry<Appointment> generateEntry(Appointment appointment) {
        Entry<Appointment> entry = new Entry<>();
        if (appointment.getRequestingUserId() == null) {
            entry.setTitle(appointment.getTitle());
        } else {
            entry.setTitle(appointment.getRequestingUserId());
        }
        entry.setUserObject(appointment);
        entry.setInterval(appointment.getRequestedDate(), appointment.getRequestedDate().plusHours(1));
        entry.titleProperty().addListener(((observable, oldValue, newValue) -> entry.getUserObject().setTitle(newValue)));
        entry.intervalProperty().addListener((observable, oldValue, newValue) -> entry.getUserObject().setRequestedDate(entry.getInterval().getStartDateTime()));
        entry.getProperties().put("quiet", true);
        return entry;
    }
}
