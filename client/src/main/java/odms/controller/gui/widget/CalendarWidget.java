package odms.controller.gui.widget;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;

public class CalendarWidget extends CalendarView {

    /**
     * Adds an entry to the correct calendar in the calendar widget based on the category of the appointment contained
     * in the entry
     *
     * @param entry entry holding the appointment data to be added to the calendar
     */
    public void addEntry(Entry<Appointment> entry) {
        if (entry == null) throw new AssertionError();
        if (entry.getUserObject() == null) throw new AssertionError();

        if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.GENERAL_CHECK_UP)) {
            addEntryToCalendar(getGeneralCheckUpCalendar(), entry);
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.BLOOD_TEST)) {
            addEntryToCalendar(getBloodTestCalendar(), entry);
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.HEALTH_ADVICE)) {
            addEntryToCalendar(getHealthAdvice(), entry);
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.PRESCRIPTION_RENEWAL)) {
            addEntryToCalendar(getPrescription(), entry);
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.OTHER)) {
            addEntryToCalendar(getOther(), entry);
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.PERSONAL)) {
            addEntryToCalendar(getPersonalCalendar(), entry);
        }
    }

    /**
     * Makes the calendar editable, adds the entry to it, and then disables the calendar if the calendar isn't the
     * personal calendar
     * @param calendar calendar to add the entry to
     * @param entry entry to add to the calendar
     */
    private void addEntryToCalendar(Calendar calendar, Entry<Appointment> entry) {
        calendar.setReadOnly(false);
        calendar.addEntry(entry);
        if (!calendar.equals(getPersonalCalendar())) {
            calendar.setReadOnly(true);
        }
    }

    /**
     * @return The calendar object titled 'Other'
     */
    private Calendar getOther() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.OTHER.toString())).get(0);
    }

    /**
     * @return The calendar object titled 'Prescription renewal'
     */
    private Calendar getPrescription() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.PRESCRIPTION_RENEWAL.toString())).get(0);
    }

    /**
     * @return The calendar object titled 'Health Advice'
     */
    private Calendar getHealthAdvice() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.HEALTH_ADVICE.toString())).get(0);
    }

    /**
     * @return The calendar object titled 'Blood Test'
     */
    private Calendar getBloodTestCalendar() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.BLOOD_TEST.toString())).get(0);
    }

    /**
     * @return The calendar object titled 'General Check up'
     */
    private Calendar getGeneralCheckUpCalendar() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.GENERAL_CHECK_UP.toString())).get(0);
    }

    /**
     * @return The calendar object titled 'Personal'
     */
    private Calendar getPersonalCalendar() {
        return getCalendarSources().filtered(cs ->
                cs.getName().equals("Default")).get(0).getCalendars().filtered(c ->
                c.getName().equals("Personal")).get(0);
    }

    /**
     * @return The calendar source object titled 'Appointments'
     */
    private CalendarSource getCalendarSource() {
        return getCalendarSources().filtered(cs -> cs.getName().equals("Appointments")).get(0);
    }
}
