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
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
    public void postAppointmentShouldReturnAcceptedIfConnectionValid() {
        ResponseEntity res = controller.postAppointment(testAppointment);
        Assert.assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postAppointment(testAppointment);
    }

    @Test
    public void getClinicianAppointmentsShouldReturnSingleAppointmentRequest() throws SQLException {
        Collection<Appointment> appointments = new ArrayList<>();
        appointments.add(testAppointment);
        when(handler.getAppointments(any(Connection.class), anyString(), any(UserType.class), anyInt(), anyInt())).thenReturn(appointments);
        Collection<Appointment> res = controller.getClinicianAppointments(0, 1, "");
        Assert.assertEquals(testAppointment, res.iterator().next());
        Assert.assertEquals(1, res.size());
    }

    @Test(expected = ServerDBException.class)
    public void getClinicianAppointmentsShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getClinicianAppointments(0, 1, "");
    }

    @Test
    public void putAppointmentShouldReturnAcceptedIfConnectionValid() {
        testAppointment.setAppointmentId(100);
        ResponseEntity res = controller.putAppointment("0", 100, testAppointment);
        Assert.assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.putAppointment("0", 100, testAppointment);
    }

}
