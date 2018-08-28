package strategies;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.database.db_strategies.AppointmentUpdateStrategy;
import odms.commons.model.Administrator;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import test_utils.DBHandlerMocker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AppointmentStrategyTest {

    private DBHandler dbHandler;
    private Connection connection;
    private PreparedStatement mockStmt;
    private User testUser = new User("Eiran", LocalDate.of(2018, 2, 20), "ABC1111");
    private Clinician testClinician = new Clinician("Jon", "16", "password");
    private AppointmentUpdateStrategy appointmentStrategy;


    @Before
    public void beforeTest() throws SQLException {
        testUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dbHandler = mock(DBHandler.class); //new DBHandler();
        connection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        appointmentStrategy = mock(AppointmentUpdateStrategy.class);

        when(dbHandler.getAppointmentStrategy()).thenReturn(appointmentStrategy);
        when(connection.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockStmt).setString(anyInt(), anyString());
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }

    @Ignore // todo find out why executeUpdate is not being called
    @Test
    public void testCreateAppointment() throws SQLException {
        LocalDateTime testDate = LocalDateTime.now().plusDays(2);
        Appointment testAppointment = new Appointment(testUser, testClinician, AppointmentCategory.GENERAL_CHECK_UP, testDate, "Help", AppointmentStatus.PENDING);

        appointmentStrategy.postSingleAppointment(connection, testAppointment);
        verify(mockStmt, times(1)).executeUpdate();
    }
}
