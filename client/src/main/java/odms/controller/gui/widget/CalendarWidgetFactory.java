package odms.controller.gui.widget;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.VirtualGrid;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.Log;
import odms.controller.AppController;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * class that holds the configurations of
 * the calendar.
 */
public class CalendarWidgetFactory {

    private CalendarWidgetFactory() {
    }

    /**
     * creates a calendar based on the calendar configurations
     *
     * @return a calendar object
     */
    public static CalendarWidget createCalendar() {
        CalendarWidget calendarView = new CalendarWidget();
        //calendarView.getCalendarSources().clear(); //removes default calendar
        calendarView.setHeader(null);
        calendarView.setFooter(null);
        calendarView.setShowAddCalendarButton(false);

        Calendar personal = calendarView.getCalendarSources().get(0).getCalendars().get(0);
        personal.setName("Personal");

        CalendarSource appointmentCategories = new CalendarSource("Appointments");

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

        calendarView.showWeekPage();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowSearchResultsTray(false);
        calendarView.setShowPrintButton(false);
        calendarView.endTimeProperty().setValue(LocalTime.of(18, 0));
        calendarView.startTimeProperty().setValue(LocalTime.of(8, 0));
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                    calendarView.setTime(LocalTime.now());
                } catch (InterruptedException e) {
                    Log.info("Thread interrupted. Message:" + e.getMessage());
                }

            }
        });
        thread.setDaemon(true);
        thread.start();

        appointmentCategories.getCalendars().forEach(c -> c.setReadOnly(true));

        calendarView.setEntryFactory(param -> {
            DateControl control = param.getDateControl();

            VirtualGrid grid = control.getVirtualGrid();
            ZonedDateTime time = param.getZonedDateTime();
            DayOfWeek firstDayOfWeek = calendarView.getFirstDayOfWeek();
            ZonedDateTime lowerTime = grid.adjustTime(time, false, firstDayOfWeek);
            ZonedDateTime upperTime = grid.adjustTime(time, true, firstDayOfWeek);

            if (Duration.between(time, lowerTime).abs().minus(Duration.between(time, upperTime).abs()).isNegative()) {
                time = lowerTime;
            } else {
                time = upperTime;
            }

            Entry<Appointment> entry = new Entry<>("Untitled");
            entry.titleProperty().addListener(((observable, oldValue, newValue) -> entry.getUserObject().setRequestingUserId(newValue)));
            entry.startTimeProperty().addListener(((observable, oldValue, newValue) -> entry.getUserObject().setRequestedDate(LocalDateTime.of(entry.getStartDate(), entry.getStartTime()))));
            entry.startDateProperty().addListener((observable, oldValue, newValue) -> entry.getUserObject().setRequestedDate(LocalDateTime.of(entry.getStartDate(), entry.getStartTime())));
            entry.setUserObject(new Appointment(entry.getTitle(), "0", AppointmentCategory.OTHER, entry.getStartAsLocalDateTime(), "", AppointmentStatus.ACCEPTED_SEEN));
            Interval interval = new Interval(time.toLocalDateTime(), time.toLocalDateTime().plusHours(1));
            entry.setInterval(interval);

            if (control instanceof AllDayView) {
                entry.setFullDay(true);
            }

            return entry;
        });

        personal.addEventHandler(evt -> {
            if (evt.isEntryAdded()) {
                AppController.getInstance().getAppointmentsBridge().postAppointment((Appointment) evt.getEntry().getUserObject());
            }
            if (evt.isEntryRemoved()) {

            }

            calendarView.getCalendarSources().forEach(cs -> cs.getCalendars().forEach(c -> {
                Entry<Appointment> entry = (Entry<Appointment>) evt.getEntry();
                for (List<Entry<?>> list : c.findEntries(entry.getStartDate(), entry.getEndDate(), entry.getZoneId()).values()) {
                    list.remove(entry);
                    for (Entry<?> e : list) {
                        if (entry.intersects(e)) {
                            entry.changeStartTime(((Appointment) evt.getOldUserObject()).getRequestedDate().toLocalTime(), true);
                        }
                    }
                }
            }));

        });

        calendarView.setVirtualGrid(new VirtualGrid("hour-grid", "h-grid", ChronoUnit.HOURS, 1));

        calendarView.getCalendarSources().forEach(cs -> cs.getCalendars().forEach(c -> {
            c.setLookAheadDuration(Duration.ofDays(365));
            c.setLookBackDuration(Duration.ofDays(365));
        }));

        return calendarView;
    }
}
