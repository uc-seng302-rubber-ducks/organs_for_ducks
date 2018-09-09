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
    public void testCheckUserAppointmentStatusExistsReturnsTrue() throws SQLException {
        when(handler.checkAppointmentStatusExists(connection, "ABC1234", 0, UserType.USER)).thenReturn(true);
        boolean result = controller.userAppointmentStatusExists("ABC1234", 0);
        Assert.assertTrue(result);
    }

    @Test
    public void testCheckUserAppointmentStatusExistsReturnsFalse() throws SQLException {
        when(handler.checkAppointmentStatusExists(connection, "ABC1234", 0, UserType.USER)).thenReturn(false);
        boolean result = controller.clinicianAppointmentStatusExists("ABC1234", 0);
        Assert.assertFalse(result);
    }

    @Test(expected = ServerDBException.class)
    public void testCheckUserAppointmentStatusExistsThrowsException() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.userAppointmentStatusExists("ABC1234", 0);
    }

    @Test
    public void testCheckClinicianAppointmentStatusExistsReturnsTrue() throws SQLException {
        when(handler.checkAppointmentStatusExists(connection, "staff1", 0, UserType.CLINICIAN)).thenReturn(true);
        boolean result = controller.clinicianAppointmentStatusExists("staff1", 0);
        Assert.assertTrue(result);
    }

    @Test
    public void testCheckClinicianAppointmentStatusExistsReturnsFalse() throws SQLException {
        when(handler.checkAppointmentStatusExists(connection, "staff1", 0, UserType.CLINICIAN)).thenReturn(false);
        boolean result = controller.clinicianAppointmentStatusExists("staff1", 0);
        Assert.assertFalse(result);
    }

    @Test(expected = ServerDBException.class)
    public void testCheckClinicianAppointmentStatusExistsThrowsException() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.clinicianAppointmentStatusExists("staff1", 0);
    }

    @Test
    public void postAppointmentShouldReturnAcceptedIfConnectionValid() throws SQLException {
        ResponseEntity res = controller.postAppointment(testAppointment);
        Assert.assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postAppointment(testAppointment);
    }

    @Test
    public void patchAppointmentStatusShouldReturnAcceptedIfConnectionValid() throws SQLException {
        when(handler.getAppointmentStatus(connection, 0)).thenReturn(2);
        ResponseEntity res = controller.patchAppointmentStatus(7, 0);
        Assert.assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void patchAppointmentStatusShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.patchAppointmentStatus(8, 0);
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

    @Test
    public void getUnseenUserAppointmentShouldReturnAppointmentIfConnectionValid() throws SQLException {
        when(handler.getUnseenAppointment(any(Connection.class), eq("ABC1234"))).thenReturn(testAppointment);
        Appointment actual = controller.getUnseenUserAppointments( "ABC1234");
        Assert.assertEquals(testAppointment, actual);
    }

    @Test(expected = ServerDBException.class)
    public void getUnseenUserAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getUnseenUserAppointments("ABC1234");
    }

    @Test
    public void deleteUsersCancelledAppointmentsShouldReturnOKIfConnectionValid() throws SQLException {
        ResponseEntity res = controller.deleteUsersCancelledAppointments("ABC1234");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void deleteUsersCancelledAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteUsersCancelledAppointments("ABC1234");
    }

    @Test(expected = ServerDBException.class)
    public void deleteCliniciansCancelledAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteCliniciansCancelledAppointments("staff1");
    }

    @Test
    public void deleteCliniciansCancelledAppointmentsShouldReturnOKIfConnectionValid() throws SQLException {
        ResponseEntity res = controller.deleteCliniciansCancelledAppointments("staff1");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void deleteAppointmentShouldReturnOKIfConnectionValid() throws SQLException {
        testAppointment.setAppointmentId(1);
        ResponseEntity res = controller.deleteAppointment(testAppointment);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void deleteAppointmentShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteAppointment(testAppointment);
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsTrue_OnAcceptedSeen() throws SQLException {
        int currentStatus = 2;
        int newStatus = 7;
        when(driver.getConnection()).thenReturn(connection);
        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);

        Assert.assertTrue(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsTrue_OnRejectedSeen() throws SQLException {
        int currentStatus = 3;
        int newStatus = 8;
        when(driver.getConnection()).thenReturn(connection);
        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);

        Assert.assertTrue(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsFalse_CurrentNotMatchNew1() throws SQLException {
        int currentStatus = 2;
        int newStatus = 8;
        when(driver.getConnection()).thenReturn(connection);
        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);

        Assert.assertFalse(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsFalse_CurrentNotMatchNew2() throws SQLException {
        int currentStatus = 3;
        int newStatus = 7;
        when(driver.getConnection()).thenReturn(connection);
        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);

        Assert.assertFalse(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsTrue_OnCancelledByUser() {
        int newStatus = AppointmentStatus.CANCELLED_BY_USER.getDbValue();
        Assert.assertTrue(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsTrue_OnCancelledByClinician() {
        int newStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();
        Assert.assertTrue(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsTrue_OnCancelledByUserSeen() throws SQLException {
        int currentStatus = AppointmentStatus.CANCELLED_BY_USER.getDbValue();
        int newStatus = AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue();

        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);
        Assert.assertTrue(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsFalse_OnCancelledByUserSeen() throws SQLException {
        int currentStatus = AppointmentStatus.PENDING.getDbValue();
        int newStatus = AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue();

        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);
        Assert.assertFalse(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsTrue_OnCancelledByClinicianSeen() throws SQLException {
        int currentStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();
        int newStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue();

        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);
        Assert.assertTrue(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test
    public void testCheckStatusUpdateAllowed_ReturnsFalse_OnCancelledByClinicianSeen() throws SQLException {
        int currentStatus = AppointmentStatus.PENDING.getDbValue();
        int newStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue();

        when(handler.getAppointmentStatus(connection, 0)).thenReturn(currentStatus);
        Assert.assertFalse(controller.checkStatusUpdateAllowed(0, newStatus));
    }

    @Test(expected = ServerDBException.class)
    public void testCheckStatusUpdateAllowedShouldThrowExceptionWhenNoConnection() throws SQLException {
        int newStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue();
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.checkStatusUpdateAllowed(0, newStatus);
    }

}
