package odms.controller.gui.widget;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;

public class CalendarWidget extends CalendarView {

    public boolean addEntry(Entry<Appointment> entry) {
        if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.GENERAL_CHECK_UP)) {
            addEntryToCalendar(getGeneralCheckUpCalendar(), entry);
            return true;
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.BLOOD_TEST)) {
            addEntryToCalendar(getBloodTestCalendar(), entry);
            return true;
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.HEALTH_ADVICE)) {
            addEntryToCalendar(getHealthAdvice(), entry);
            return true;
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.PRESCRIPTION_RENEWAL)) {
            addEntryToCalendar(getPrescription(), entry);
            return true;
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.OTHER)) {
            addEntryToCalendar(getOther(), entry);
            return true;
        } else if (entry.getUserObject().getAppointmentCategory().equals(AppointmentCategory.PERSONAL)) {
            addEntryToCalendar(getPersonalCalendar(), entry);
        }
        return false;
    }

    private void addEntryToCalendar(Calendar calendar, Entry<Appointment> entry) {
        calendar.setReadOnly(false);
        calendar.addEntry(entry);
        calendar.setReadOnly(true);
    }

    private Calendar getOther() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.OTHER.toString())).get(0);
    }

    private Calendar getPrescription() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.PRESCRIPTION_RENEWAL.toString())).get(0);
    }

    private Calendar getHealthAdvice() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.HEALTH_ADVICE.toString())).get(0);
    }

    private Calendar getBloodTestCalendar() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.BLOOD_TEST.toString())).get(0);
    }

    private Calendar getGeneralCheckUpCalendar() {
        return getCalendarSource().getCalendars().filtered(c -> c.getName().equals(AppointmentCategory.GENERAL_CHECK_UP.toString())).get(0);
    }

    private Calendar getPersonalCalendar() {
        return getCalendarSources().filtered(cs ->
                cs.getName().equals("Default")).get(0).getCalendars().filtered(c ->
                c.getName().equals("Personal")).get(0);
    }

    private CalendarSource getCalendarSource() {
        return getCalendarSources().filtered(cs -> cs.getName().equals("Appointments")).get(0);
    }
}
