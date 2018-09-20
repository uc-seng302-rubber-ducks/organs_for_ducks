package odms.controller;

import odms.commons.model.datamodel.BloodTest;
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
    private BloodTest testBloodTest;

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        socketHandler = mock(SocketHandler.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        when(handler.getBloodTests(any(Connection.class), anyString(),any(LocalDate.class), any(LocalDate.class))).thenReturn(new ArrayList<>());
        controller = new BloodTestController(manager, socketHandler);
        testBloodTest = new BloodTest();
    }

    @Test(expected = BadRequestException.class)
    public void incorrectEndDateShouldNotParse(){
        controller.getBloodTests("a","01/01/1999", "1999/01/01");
        verify(handler,times(0)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class));
    }


    @Test(expected = BadRequestException.class)
    public void incorrectStartDateShouldNotParse(){
        controller.getBloodTests("a","1999/01/01", "01/01/1999");
        verify(handler,times(0)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class));
    }


    @Test
    public void correctDateShouldParse(){
        controller.getBloodTests("a","01/01/1999", "01/02/1999");

        verify(handler,times(1)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class));
    }

}
