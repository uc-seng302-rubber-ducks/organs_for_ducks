package odms.controller.userDetailsControllerTests;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.controller.user.details.ProcedureController;
import odms.exception.ServerDBException;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcedureControllerTest {

    private ProcedureController controller;
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
        controller = new ProcedureController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void postProcedureShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        ResponseEntity res = controller.postMedicalProcedure("ABC1234", new MedicalProcedure(LocalDate.of(2017, 12, 3), "Lung Transplant", "Transplantation of lung", new ArrayList<>()));
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void postProcedureWithNoUserShouldReturnNotFound() {
        ResponseEntity res = controller.postMedicalProcedure("ANC1111", new MedicalProcedure(LocalDate.of(2017, 12, 3), "Lung Transplant", "moving of lungs", new ArrayList<>()));
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postProcedureShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postMedicalProcedure("ABC1234", new MedicalProcedure(LocalDate.of(2017, 12, 3), "Lung Transplant", "moving of lungs", new ArrayList<>()));
    }

    @Test
    public void putProceduresShouldReturnOK() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        List<MedicalProcedure> procedures = new ArrayList<>(Arrays.asList(new MedicalProcedure(LocalDate.now(), "test procedure", "tester", new ArrayList<>()),
                new MedicalProcedure(LocalDate.of(2018, 2, 25), "second test", "experimenting", new ArrayList<>())));
        ResponseEntity res = controller.putProcedure("ABC1234", procedures);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void putProceduresShouldReturnNotFoundWhenNoUser() {
        List<MedicalProcedure> procedures = new ArrayList<>(Arrays.asList(new MedicalProcedure(LocalDate.now(), "test procedure", "tester", new ArrayList<>()),
                new MedicalProcedure(LocalDate.of(2018, 2, 25), "second test", "experimenting", new ArrayList<>())));
        ResponseEntity res = controller.putProcedure("ABC1111", procedures);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putProceduresShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        List<MedicalProcedure> procedures = new ArrayList<>(Arrays.asList(new MedicalProcedure(LocalDate.now(), "test procedure", "tester", new ArrayList<>()),
                new MedicalProcedure(LocalDate.of(2018, 2, 25), "second test", "experimenting", new ArrayList<>())));
        controller.putProcedure("ABC1234", procedures);
    }
}
