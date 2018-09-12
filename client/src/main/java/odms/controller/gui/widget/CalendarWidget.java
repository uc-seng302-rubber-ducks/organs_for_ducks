package odms.controller.gui.widget;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import odms.commons.model._enum.AppointmentCategory;

import java.time.LocalTime;

/**
 * class that holds the configurations of
 * the calendar.
 */
public class CalendarWidget {

    /**
     * creates a calendar based on the calendar configurations
     *
     * @return a calendar object
     */
    public static CalendarView createCalendar() {
        CalendarView calendarView = new CalendarView();
        calendarView.getCalendarSources().clear(); //removes default calendar
        calendarView.setHeader(null);
        calendarView.setFooter(null);
        calendarView.setShowAddCalendarButton(false);

        CalendarSource appointmentCategories = new CalendarSource("Appointment Categories");

        Calendar bloodTestCalendar = new Calendar(AppointmentCategory.BLOOD_TEST.toString());
        bloodTestCalendar.setStyle(Calendar.Style.STYLE1);

        Calendar generalCheckUpCalendar = new Calendar(AppointmentCategory.GENERAL_CHECK_UP.toString());
        generalCheckUpCalendar.setStyle(Calendar.Style.STYLE2);

        Calendar healthAdviceCalendar = new Calendar(AppointmentCategory.HEALTH_ADVICE.toString());
        healthAdviceCalendar.setStyle(Calendar.Style.STYLE3);

        Calendar prescriptionCalendar = new Calendar(AppointmentCategory.PRESCRIPTION_RENEWAL.toString());
        prescriptionCalendar.setStyle(Calendar.Style.STYLE4);

        Calendar otherCalendar = new Calendar(AppointmentCategory.OTHER.toString());
        otherCalendar.setStyle(Calendar.Style.STYLE5);

        appointmentCategories.getCalendars().addAll(bloodTestCalendar, generalCheckUpCalendar, healthAdviceCalendar, prescriptionCalendar, otherCalendar);
        calendarView.getCalendarSources().add(appointmentCategories);
        calendarView.setRequestedTime(LocalTime.now());
        calendarView.setStartTime(LocalTime.of(8, 0));
        calendarView.setEndTime(LocalTime.of(18, 0)); //6pm
        return calendarView;
    }
}
