package odms.controller;

import odms.commons.model.datamodel.BloodTest;
import odms.database.BloodTestHandler;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.BadRequestException;
import odms.exception.ServerDBException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
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
        when(bloodTestHandler.getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        controller = new BloodTestController(manager, socketHandler);
        testBloodTest = new BloodTest();
    }

    @Test(expected = BadRequestException.class)
    public void incorrectEndDateShouldNotParse() throws SQLException {
        controller.getBloodTests("a", "1999-01-01", "01-01-1999", 30, 0);
        verify(bloodTestHandler, times(0)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), anyInt(), anyInt());
    }

    @Test(expected = BadRequestException.class)
    public void incorrectStartDateShouldNotParse() throws SQLException {
        controller.getBloodTests("a", "01-01-1999", "1999-01-01", 30, 0);
        verify(bloodTestHandler, times(0)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), anyInt(), anyInt());
    }

    @Test
    public void correctDateShouldParse() throws SQLException {
        controller.getBloodTests("a", "1999-01-01", "1999-02-01", 30, 0);
        verify(bloodTestHandler, times(1)).getBloodTests(any(Connection.class), anyString(), any(LocalDate.class), any(LocalDate.class), anyInt(), anyInt());
    }

    @Test(expected = ServerDBException.class)
    public void testGetAllBloodTestsThrowsExceptionIfNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getBloodTests("ABC1234", "1999-01-01", "1999-01-01", 30, 0);
    }

    @Test
    public void testGetSingleBloodTestReturnsBloodTest() throws SQLException {
        controller.getBloodTest("ABC1234", 1);
        verify(bloodTestHandler, times(1)).getBloodTest(any(Connection.class), anyString(), anyInt());
    }

    @Test(expected = ServerDBException.class)
    public void testGetSingleBloodTestThrowsExceptionIfNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getBloodTest("ABC1234", 1);
    }

    @Test
    public void testPostBloodTestReturnsCreatedResponse() {
        ResponseEntity response = controller.postBloodTest("ABC1234", testBloodTest);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void testPostBloodTestThrowsExceptionIfNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postBloodTest("ABC1234", testBloodTest);
    }

    @Test
    public void testDeleteBloodTestReturnsOkResponse() throws IOException {
        ResponseEntity response = controller.deleteBloodTest("ABC1234", 1);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void testDeleteBloodTestThrowsExceptionIfNoConnection() throws SQLException, IOException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteBloodTest("ABC1234", 1);
    }

    @Test
    public void testPatchBloodTestReturnsOkResponse() throws IOException {
        ResponseEntity response = controller.patchBloodTest("ABC1234", 1, testBloodTest);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void testPatchBloodTestThrowsExceptionIfNoConnection() throws SQLException, IOException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.patchBloodTest("ABC1234", 1, testBloodTest);
    }

}
