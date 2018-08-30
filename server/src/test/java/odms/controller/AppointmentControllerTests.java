package odms.controller;


import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.database.db_strategies.AppointmentUpdateStrategy;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;
import odms.exception.ServerDBException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppointmentControllerTests {

    private AppointmentController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private SocketHandler socketHandler;
    private Appointment testAppointment;

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        socketHandler = mock(SocketHandler.class);
        AppointmentUpdateStrategy strategy = mock(AppointmentUpdateStrategy.class);

        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        when(handler.getAppointmentStrategy()).thenReturn(strategy);

        controller = new AppointmentController(manager, socketHandler);
        LocalDateTime testDate = LocalDateTime.now().plusDays(2);
        testAppointment = new Appointment("ABC1234", "0", AppointmentCategory.GENERAL_CHECK_UP, testDate, "Help", AppointmentStatus.PENDING);
    }

    @Test
    public void postAppointmentShouldReturnAcceptedIfConnectionValid() throws SQLException {
        ResponseEntity res = controller.postAppointment(testAppointment);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
    }

    @Test(expected = ServerDBException.class)
    public void postAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postAppointment(testAppointment);
    }

    @Test
    public void getUserAppointmentShouldReturnCollectionIfConnectionValid() throws SQLException {
        when(handler.getAppointments(any(Connection.class), eq("ABC1234"), any(UserType.class), anyInt(), anyInt())).thenReturn(Collections.singleton(testAppointment));
        Collection<Appointment> appointments = controller.getUserAppointments(30, 0, "ABC1234");
        Assert.assertTrue(appointments.size() == 1);
    }

    @Test(expected = ServerDBException.class)
    public void getUserAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getUserAppointments(30, 0, "ABC1234");
    }

    @Test
    public void getClinicianAppointmentShouldReturnCollectionIfConnectionValid() throws SQLException {
        when(handler.getAppointments(any(Connection.class), eq("0"), any(UserType.class), anyInt(), anyInt())).thenReturn(Collections.singleton(testAppointment));
        Collection<Appointment> appointments = controller.getClinicianAppointments(30, 0, "0");
        Assert.assertTrue(appointments.size() == 1);
    }

    @Test(expected = ServerDBException.class)
    public void getClinicianAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getClinicianAppointments(30, 0, "0");
    }

}
