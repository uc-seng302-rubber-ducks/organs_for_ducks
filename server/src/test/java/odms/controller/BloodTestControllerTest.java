package odms.controller;

import odms.commons.model.datamodel.BloodTest;
import odms.database.BloodTestHandler;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.BadRequestException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BloodTestControllerTest {

    private Connection connection;
    private DBManager manager;
    private DBHandler handler;
    private JDBCDriver driver;
    private SocketHandler socketHandler;
    private BloodTestController controller;
    private BloodTestHandler bloodTestHandler;
    private BloodTest testBloodTest;

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        bloodTestHandler = mock(BloodTestHandler.class);
        driver = mock(JDBCDriver.class);
        socketHandler = mock(SocketHandler.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(handler.getBloodTestHandler()).thenReturn(bloodTestHandler);
        when(manager.getDriver()).thenReturn(driver);
        when(bloodTestHandler.getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), count, startIndex)).thenReturn(new ArrayList<>());
        controller = new BloodTestController(manager, socketHandler);
        testBloodTest = new BloodTest();
    }

    @Test(expected = BadRequestException.class)
    public void incorrectEndDateShouldNotParse() throws SQLException {
        controller.getBloodTests("a","01/01/1999", "1999/01/01");
        verify(bloodTestHandler, times(0)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), count, startIndex);
    }


    @Test(expected = BadRequestException.class)
    public void incorrectStartDateShouldNotParse() throws SQLException {
        controller.getBloodTests("a","1999/01/01", "01/01/1999");
        verify(bloodTestHandler, times(0)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), count, startIndex);
    }


    @Test
    public void correctDateShouldParse() throws SQLException {
        controller.getBloodTests("a","01/01/1999", "01/02/1999");

        verify(bloodTestHandler, times(1)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), count, startIndex);
    }

}
