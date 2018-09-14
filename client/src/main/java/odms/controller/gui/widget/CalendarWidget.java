package odms.controller.gui.widget;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.page.DayPage;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.utils.Log;

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
        calendarView.getCalendarSources().clear();
        calendarView.getCalendarSources().add(appointmentCategories);
        calendarView.setRequestedTime(LocalTime.now());
        calendarView.setStartTime(LocalTime.of(8, 0));
        calendarView.setEndTime(LocalTime.of(18, 0)); //6pm

        calendarView.getDayPage().setDayPageLayout(DayPage.DayPageLayout.DAY_ONLY);
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
        calendarView.getWeekPage().onMouseClickedProperty().unbind();
        calendarView.getWeekPage().entryFactoryProperty().unbind();

        calendarView.getWeekPage().getDetailedWeekView().onMouseClickedProperty().unbind();
        calendarView.getWeekPage().getDetailedWeekView().entryFactoryProperty().unbind();
        calendarView.getWeekPage().getDetailedWeekView().onMouseReleasedProperty().unbind();

        calendarView.getWeekPage().getDetailedWeekView().getWeekView().entryFactoryProperty().unbind();
        calendarView.getWeekPage().getDetailedWeekView().getWeekView().onMouseClickedProperty().unbind();
        calendarView.getWeekPage().getDetailedWeekView().getWeekView().onMouseReleasedProperty().unbind();

        calendarView.getDayPage().entryFactoryProperty().unbind();
        calendarView.getDayPage().onMouseClickedProperty().unbind();
        calendarView.getDayPage().onMouseReleasedProperty().unbind();

        calendarView.getDayPage().getDetailedDayView().entryFactoryProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().onMouseClickedProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().onMouseReleasedProperty().unbind();

        calendarView.getDayPage().getDetailedDayView().getDayView().entryFactoryProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().getDayView().onMouseClickedProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().getDayView().onMouseReleasedProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().getDayView().entryViewFactoryProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().getDayView().entryEditPolicyProperty().unbind();
        calendarView.getDayPage().getDetailedDayView().getDayView().setOnMouseClicked(Event::consume);

        calendarView.getDayPage().getDetailedDayView().getDayView().getBoundDateControls().clear();
        ((ObservableList<DateControl>) calendarView.getDayPage().getDetailedDayView().getDayView().getBoundDateControls()).addListener((ListChangeListener<DateControl>) c -> c.getList().clear());


        calendarView.getDayPage().getDetailedDayView().getDayView().setStyle("-fx-background-color: GREEN");

        //.setOnMouseClicked(e -> System.out.println("aaaaa"));

        calendarView.entryFactoryProperty().unbind();
        calendarView.onMouseClickedProperty().unbind();
        calendarView.onMouseReleasedProperty().unbind();



        return calendarView;
    }
}
