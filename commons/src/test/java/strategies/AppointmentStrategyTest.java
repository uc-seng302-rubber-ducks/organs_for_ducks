package strategies;

import odms.commons.database.DBHandler;
import odms.commons.database.db_strategies.AppointmentUpdateStrategy;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
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
}
