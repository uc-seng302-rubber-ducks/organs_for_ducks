package odms.commons.database;

import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;
import odms.commons.model._enum.db.appointment.statements.AppointmentStatement;
import org.junit.Before;
import org.junit.Test;
import test_utils.DBHandlerMocker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AppointmentFetchStrategyTest {

    private DBHandler handler;
    private Connection connection;
    private ResultSet resultSet;

    @Before
    public void setUp() throws SQLException {
        handler = new DBHandler();
        connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        resultSet = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    public void testUserTypeCreatesCorrectStrategy() throws SQLException {
        handler.getAppointments(connection, "ABC1234", UserType.USER, 30, 0);
        verify(connection, atLeastOnce()).prepareStatement(AppointmentStatement.GET_APPTS_FOR_USER.getStatement());
    }

    @Test
    public void testClinicianTypeCreatesCorrectStrategy() throws SQLException {
        handler.getAppointments(connection, "0", UserType.CLINICIAN, 30, 0);
        verify(connection, atLeastOnce()).prepareStatement(AppointmentStatement.GET_APPTS_FOR_CLINICIAN.getStatement());
    }

    @Test
    public void testCorrectAppointmentIsMadeWhenUserCalled() throws SQLException {
        DBHandlerMocker.setAppointmentDetails(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        ArrayList<Appointment> appointments = new ArrayList<>(handler.getAppointments(connection, "ABC1234", UserType.USER, 30, 0));
        assertTrue(appointments.size() == 1);
        checkAppointment(appointments.get(0));
    }

    @Test
    public void testCorrectAppointmentIsMadeWhenClinicianCalled() throws SQLException {
        DBHandlerMocker.setAppointmentDetails(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        ArrayList<Appointment> appointments = new ArrayList<>(handler.getAppointments(connection, "0", UserType.CLINICIAN, 30, 0));
        assertTrue(appointments.size() == 1);
        checkAppointment(appointments.get(0));
    }

    private void checkAppointment(Appointment appointment) {
        assertEquals(0, appointment.getAppointmentId());
        assertEquals(LocalDateTime.of(2018, 12, 10, 15, 3), appointment.getRequestedDate());
        assertEquals("ABC1234", appointment.getRequestingUserId());
        assertEquals("0", appointment.getRequestedClinicianId());
        assertEquals(AppointmentCategory.BLOOD_TEST, appointment.getAppointmentCategory());
        assertEquals(AppointmentStatus.PENDING, appointment.getAppointmentStatus());
        assertEquals("A description", appointment.getRequestDescription());
    }

}
