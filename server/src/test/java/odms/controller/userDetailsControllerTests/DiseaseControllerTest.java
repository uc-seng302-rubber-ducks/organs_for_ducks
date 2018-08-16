package odms.controller.userDetailsControllerTests;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.controller.user.details.DiseaseController;
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

public class DiseaseControllerTest {

    private DiseaseController controller;
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
        controller = new DiseaseController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void postDiseaseShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        ResponseEntity res = controller.postDisease("ABC1234", new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10)));
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void postDiseaseWithNoUserShouldReturnNotFound() {
        ResponseEntity res = controller.postDisease("ANC1111", new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10)));
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postDiseaseShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postDisease("ABC1234", new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10)));
    }

    @Test
    public void putDiseasesShouldReturnOK() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        List<Disease> diseases = new ArrayList<>(Arrays.asList(new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10)),
                new Disease("Conjunctivitis", false, true, LocalDate.of(2018, 5, 23))));
        ResponseEntity res = controller.putDiseases("ABC1234", diseases);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void putDiseasesShouldReturnNotFoundWhenNoUser() {
        List<Disease> diseases = new ArrayList<>(Arrays.asList(new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10)),
                new Disease("Conjunctivitis", false, true, LocalDate.of(2018, 5, 23))));
        ResponseEntity res = controller.putDiseases("ABC1111", diseases);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putDiseasesShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        List<Disease> diseases = new ArrayList<>(Arrays.asList(new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10)),
                new Disease("Conjunctivitis", false, true, LocalDate.of(2018, 5, 23))));
        controller.putDiseases("ABC1234", diseases);
    }

    @Test
    public void postCurrentDiseaseShouldAddToCorrectList() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Disease testDisease = new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10));
        controller.postDisease(testUser.getNhi(), testDisease);
        Assert.assertTrue(testUser.getCurrentDiseases().contains(testDisease));
    }

    @Test
    public void postDiseaseShouldAddToCorrectList() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Disease testDisease = new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10));
        Disease testDisease2 = new Disease("Conjunctivitis", false, true, LocalDate.of(2017, 2, 10));
        controller.putDiseases(testUser.getNhi(), Arrays.asList(testDisease2, testDisease));
        Assert.assertTrue(testUser.getPastDiseases().contains(testDisease2));
        Assert.assertTrue(testUser.getPastDiseases().size() == 1);
        Assert.assertTrue(testUser.getCurrentDiseases().contains(testDisease));
        Assert.assertTrue(testUser.getCurrentDiseases().size() == 1);
    }

    @Test
    public void putDiseasesShouldAddToCorrectLists() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Disease testDisease = new Disease("Osteoporosis", true, false, LocalDate.of(2017, 2, 10));

        controller.postDisease(testUser.getNhi(), testDisease);
        Assert.assertTrue(testUser.getCurrentDiseases().contains(testDisease));
    }

}
