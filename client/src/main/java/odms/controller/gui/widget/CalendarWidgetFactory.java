package odms.controller.gui.widget;

import com.calendarfx.model.*;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.VirtualGrid;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * class that holds the configurations of
 * the calendar.
 */
public class CalendarWidgetFactory {

    /**
     * Quiet mode to determine if the entry should cause an update call to the server or not
     */
    private static final String QUIET_MODE = "quiet";

    private static final int START_TIME = 8;
    private static final int END_TIME = 18;

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
        bloodTestCalendar.setStyle(Calendar.Style.STYLE6);

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

        calendarView.getDayPage().getDetailedDayView().getDayView().setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.SHOW_COMPRESSED);
        calendarView.getWeekPage().getDetailedWeekView().getWeekView().setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.SHOW_COMPRESSED);

        hideButtons(calendarView);

        startCalendarTimer(calendarView);

        appointmentCategories.getCalendars().forEach(c -> c.setReadOnly(true));

        setCalendarEntryFactory(calendarView);

        setPersonalEventHandler(personal, calendarView);


        // This sets the virtual grid to have a spacing of one hour to snap the entries per hour.
        calendarView.setVirtualGrid(new VirtualGrid("hour-grid", "h-grid", ChronoUnit.HOURS, 1));

        calendarView.getCalendarSources().forEach(cs -> cs.getCalendars().forEach(c -> {
            c.setLookAheadDuration(Duration.ofDays(365));
            c.setLookBackDuration(Duration.ofDays(365));
        }));
        setCalendarOnClick(calendarView);
        return calendarView;
    }

    private static void setPersonalEventHandler(Calendar personal, CalendarWidget calendarView) {
        personal.addEventHandler(evt -> {
            if (evt.isEntryAdded()) {
                if (!evt.getEntry().getProperties().containsKey(QUIET_MODE)) {
                    if (checkNoClashes(calendarView, evt.getEntry())) {
                        if (checkNotInPast((Entry<Appointment>) evt.getEntry(), evt)) {
                            AppController.getInstance().getAppointmentsBridge().postAppointment((Appointment) evt.getEntry().getUserObject());
                        } else {
                            evt.getEntry().getProperties().put(QUIET_MODE, true);
                            evt.getEntry().getCalendar().removeEntry(evt.getEntry());
                        }
                    } else {
                        AlertWindowFactory.generateError("Cannot generate an entry there as it clashes with another existing entry");
                        evt.getEntry().getProperties().put(QUIET_MODE, true);
                        evt.getEntry().getCalendar().removeEntry(evt.getEntry());
                    }
                } else {
                    evt.getEntry().getProperties().remove(QUIET_MODE);
                }
            } else if (evt.isEntryRemoved()) {
                if (!evt.getEntry().getProperties().containsKey(QUIET_MODE)) {
                    AppController.getInstance().getAppointmentsBridge().deleteAppointment((Appointment) evt.getEntry().getUserObject());
                } else {
                    evt.getEntry().getProperties().remove(QUIET_MODE);
                }
            } else if (evt.getOldInterval() != null) { // Only put if the times has changed
                Entry<Appointment> entry = (Entry<Appointment>) evt.getEntry();
                if (entry != null && !entry.getProperties().containsKey(QUIET_MODE)) {
                    if (!checkNoClashes(calendarView, entry)) {
                        entry.getProperties().put(QUIET_MODE, true);
                        entry.setInterval(evt.getOldInterval());
                        entry.getProperties().remove(QUIET_MODE);
                        AlertWindowFactory.generateInfoWindow("You cannot move this there because it clashes with another existing entry");
                    }
                    checkNotInPast(entry, evt);
                    if (!entry.getInterval().equals(evt.getOldInterval())) {
                        AppController.getInstance().getAppointmentsBridge().putAppointment(entry.getUserObject(), AppController.getInstance().getToken());
                    }
                }
            }
        });
    }

    /**
     * Checks that the entry being moved does not end up a day in the past
     *
     * @param entry entry to be checked
     */
    private static boolean checkNotInPast(Entry<Appointment> entry, CalendarEvent event) {
        if (entry.getInterval().getStartDateTime().isBefore(LocalDateTime.now())) {
            entry.getProperties().put(QUIET_MODE, true);
            if (event.getOldInterval() != null)
                entry.setInterval(event.getOldInterval());
            entry.getProperties().remove(QUIET_MODE);
            AlertWindowFactory.generateInfoWindow("You cannot have entries in the past");
            return false;
        } else if (entry.getInterval().getStartTime().isAfter(LocalTime.of(END_TIME, 0)) || entry.getInterval().getStartTime().isBefore(LocalTime.of(START_TIME, 0))) {
            entry.getProperties().put(QUIET_MODE, true);
            if (event.getOldInterval() != null)
                entry.setInterval(event.getOldInterval());
            entry.getProperties().remove(QUIET_MODE);
            AlertWindowFactory.generateInfoWindow("You cannot have entries before the hours of 8am and 6 pm");
            return false;
        }
        return true;
    }

    /**
     * Checks that the entry provided does not clash with any of the entries in the given calendar widget
     * Resets it to the
     *
     * @param calendarView calendarview to check the entry against
     * @param entry        entry to check
     */
    private static boolean checkNoClashes(CalendarWidget calendarView, Entry entry) {
        for (CalendarSource cs : calendarView.getCalendarSources()) {
            for (Calendar c : cs.getCalendars()) {
                for (List<Entry<?>> list : c.findEntries(entry.getStartDate(), entry.getEndDate(), entry.getZoneId()).values()) {
                    for (Entry<?> e : list) {
                        if (entry.intersects(e) && !e.equals(entry)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Hides buttons from the user to prevent the breaking of things
     *
     * @param calendarView calendar view to hide buttons of
     */
    private static void hideButtons(CalendarWidget calendarView) {
        calendarView.showWeekPage();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowSearchResultsTray(false);
        calendarView.setShowPrintButton(false);
        calendarView.endTimeProperty().setValue(LocalTime.of(END_TIME, 0));
        calendarView.startTimeProperty().setValue(LocalTime.of(START_TIME, 0));
    }

    /**
     * Starts a new thread which updates the current time of the calendar
     *
     * @param calendarView calendar view to update
     */
    private static void startCalendarTimer(CalendarWidget calendarView) {
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
    }


    /**
     * disables double-click popover menu
     *
     * @param calendarView calendarview to modify
     */
    private static void setCalendarOnClick(CalendarWidget calendarView) {
        calendarView.setEntryDetailsCallback(param -> null);
    }

    /**
     * Sets the entry factory of the given calendar widget to be in line with what we want, which is an entry holding an appointment
     *
     * @param calendarView calendarview to set entry factory of
     */
    private static void setCalendarEntryFactory(CalendarWidget calendarView) {
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
            entry.titleProperty().addListener(((observable, oldValue, newValue) -> entry.getUserObject().setTitle(newValue)));
            entry.intervalProperty().addListener((observable, oldValue, newValue) -> entry.getUserObject().setRequestedDate(entry.getInterval().getStartDateTime()));
            Appointment appointment = new Appointment(null, AppController.getInstance().getUsername(), AppointmentCategory.PERSONAL, entry.getStartAsLocalDateTime(), "", AppointmentStatus.ACCEPTED_SEEN);
            appointment.setTitle("Untitled");
            entry.setUserObject(appointment);
            Interval interval = new Interval(time.toLocalDateTime(), time.toLocalDateTime().plusHours(1));
            entry.setInterval(interval);

            if (control instanceof AllDayView) {
                entry.setFullDay(true);
            }

            return entry;
        });
    }
}
