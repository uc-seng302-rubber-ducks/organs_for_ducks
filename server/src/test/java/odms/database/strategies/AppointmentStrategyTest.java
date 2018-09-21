package odms.database.strategies;

import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;
import odms.database.DBHandler;
import odms.database.db_strategies.AppointmentUpdateStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AppointmentStrategyTest {

    private DBHandler dbHandler;
    private Connection connection;
    private PreparedStatement mockStmt;
    private AppointmentUpdateStrategy appointmentStrategy;


    @Before
    public void beforeTest() throws SQLException {
        dbHandler = mock(DBHandler.class); //new DBHandler();
        connection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        appointmentStrategy = new AppointmentUpdateStrategy();

        when(connection.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockStmt).setString(anyInt(), anyString());
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }

    @Test
    public void testCreateAppointment() throws SQLException {
        LocalDateTime testDate = LocalDateTime.now().plusDays(2);
        Appointment testAppointment = new Appointment("ABC1234", "id1234", AppointmentCategory.GENERAL_CHECK_UP, testDate, "Help", AppointmentStatus.PENDING);

        appointmentStrategy.postSingleAppointment(connection, testAppointment);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testPatchAppointmentStatus() throws SQLException {
        appointmentStrategy.patchAppointmentStatus(connection, 7, 0);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteRejectedSeenStatus() throws SQLException {
        appointmentStrategy.deleteRejectedSeenStatus(connection, 0);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteUsersCancelledAppointments() throws SQLException {
        appointmentStrategy.deleteCancelledAppointments(connection, "ABC1234", UserType.USER);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteCliniciansCancelledAppointments() throws SQLException {
        appointmentStrategy.deleteCancelledAppointments(connection, "staff1", UserType.CLINICIAN);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteCancelledAppointmentsFails() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        verify(mockStmt, times(0)).executeUpdate();
    }

    @Test
    public void testUpdateAppointment() throws SQLException {
        LocalDateTime testDate = LocalDateTime.now().plusDays(2);
        Appointment testAppointment = new Appointment("ABC1234", "id1234", AppointmentCategory.GENERAL_CHECK_UP, testDate, "Help", AppointmentStatus.ACCEPTED);
        testAppointment.setAppointmentId(100);

        appointmentStrategy.putSingleAppointment(connection, testAppointment);
        verify(mockStmt, times(1)).executeUpdate();
    }
}
