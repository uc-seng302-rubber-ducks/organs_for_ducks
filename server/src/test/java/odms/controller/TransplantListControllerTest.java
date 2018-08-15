package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.exception.ServerDBException;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransplantListControllerTest {

    private TransplantListController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private User testUser;

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new TransplantListController(manager);
    }
    @Test
    public void shouldReturnResultsIfAvailable() throws SQLException{
        List<TransplantDetails> expected = new ArrayList<>();
        expected.add(new TransplantDetails("ABC1234", "Geoff", Organs.HEART, LocalDate.now(), "over there", 0, "A+"));
        when(handler.getTransplantDetails(any(Connection.class), anyInt(), anyInt(), anyString(), anyString(), any(String[].class)))
                .thenReturn(expected);

        List<TransplantDetails> actual = controller.getWaitingFor(0, 0, "", "", new String[] {});

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnEmptyListIfNoResults() throws SQLException{
        when(handler.getTransplantDetails(any(Connection.class), anyInt(), anyInt(), anyString(), anyString(), any(String[].class)))
                .thenReturn(null);
        final List<TransplantDetails> expected = new ArrayList<>();

        List<TransplantDetails> actual = controller.getWaitingFor(0, 0, "", "", new String[] {});
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = ServerDBException.class)
    public void shouldThrowExceptionWhenConnectionError() throws SQLException{
        when(driver.getConnection()).thenThrow(SQLException.class);

        controller.getWaitingFor(0, 0, "", "", new String[] {});

    }

}
