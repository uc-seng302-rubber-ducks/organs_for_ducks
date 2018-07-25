package odms.controller.userDetailsControllerTests;

import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.controller.user.details.ReceivingOrgansController;
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
import java.util.EnumMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceivingOrgansControllerTest {
    private ReceivingOrgansController controller;
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
        controller = new ReceivingOrgansController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void postReceivingOrganShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving = new EnumMap<>(Organs.class);
        ArrayList<ReceiverOrganDetailsHolder> organDetailsHolders = new ArrayList<>(Arrays.asList(new ReceiverOrganDetailsHolder(LocalDate.of(2017, 6, 25), LocalDate.of(2017, 8, 30), null),
                new ReceiverOrganDetailsHolder(LocalDate.of(2017, 9, 10), LocalDate.of(2017, 9, 20), null)));
        receiving.put(Organs.LIVER, organDetailsHolders);
        ResponseEntity res = controller.postOrgansToReceive(receiving, "ABC1234");
        Assert.assertTrue(testUser.getReceiverDetails().getOrgans().containsKey(Organs.LIVER));
        Assert.assertTrue(testUser.getReceiverDetails().getOrganDates(Organs.LIVER).size() == 4);
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void postReceivingOrganShouldReturnNotFoundWhenNoUserExists() {
        EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving = new EnumMap<>(Organs.class);
        ArrayList<ReceiverOrganDetailsHolder> organDetailsHolders = new ArrayList<>(Arrays.asList(new ReceiverOrganDetailsHolder(LocalDate.of(2017, 6, 25), LocalDate.of(2017, 8, 30), null),
                new ReceiverOrganDetailsHolder(LocalDate.of(2017, 9, 10), LocalDate.of(2017, 9, 20), null)));
        receiving.put(Organs.LIVER, organDetailsHolders);
        ResponseEntity res = controller.postOrgansToReceive(receiving, "ABC1234");
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postReceivingOrganShouldThrowServerDBErrorWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving = new EnumMap<>(Organs.class);
        ArrayList<ReceiverOrganDetailsHolder> organDetailsHolders = new ArrayList<>(Arrays.asList(new ReceiverOrganDetailsHolder(LocalDate.of(2017, 6, 25), LocalDate.of(2017, 8, 30), null),
                new ReceiverOrganDetailsHolder(LocalDate.of(2017, 9, 10), LocalDate.of(2017, 9, 20), null)));
        receiving.put(Organs.LIVER, organDetailsHolders);
        controller.postOrgansToReceive(receiving, "ABC1234");
    }

    @Test
    public void putReceivingOrgansShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving = new EnumMap<>(Organs.class);
        ArrayList<ReceiverOrganDetailsHolder> organDetailsHolders = new ArrayList<>(Arrays.asList(new ReceiverOrganDetailsHolder(LocalDate.of(2017, 6, 25), LocalDate.of(2017, 8, 30), null),
                new ReceiverOrganDetailsHolder(LocalDate.of(2017, 9, 10), LocalDate.of(2017, 9, 20), null)));
        receiving.put(Organs.LIVER, organDetailsHolders);
        ResponseEntity res = controller.putOrganToReceive(receiving, "ABC1234");
        Assert.assertTrue(testUser.getReceiverDetails().getOrgans().containsKey(Organs.LIVER));
        Assert.assertTrue(testUser.getReceiverDetails().getOrganDates(Organs.LIVER).size() == 4);
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putReceivingOrgansShouldReturnNotFoundWhenNoUser() {
        EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving = new EnumMap<>(Organs.class);
        ArrayList<ReceiverOrganDetailsHolder> organDetailsHolders = new ArrayList<>(Arrays.asList(new ReceiverOrganDetailsHolder(LocalDate.of(2017, 6, 25), LocalDate.of(2017, 8, 30), null),
                new ReceiverOrganDetailsHolder(LocalDate.of(2017, 9, 10), LocalDate.of(2017, 9, 20), null)));
        receiving.put(Organs.LIVER, organDetailsHolders);
        ResponseEntity res = controller.putOrganToReceive(receiving, "ABC1234");
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putReceivingOrgansShouldThrowExceptionAndReturn500() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving = new EnumMap<>(Organs.class);
        ArrayList<ReceiverOrganDetailsHolder> organDetailsHolders = new ArrayList<>(Arrays.asList(new ReceiverOrganDetailsHolder(LocalDate.of(2017, 6, 25), LocalDate.of(2017, 8, 30), null),
                new ReceiverOrganDetailsHolder(LocalDate.of(2017, 9, 10), LocalDate.of(2017, 9, 20), null)));
        receiving.put(Organs.LIVER, organDetailsHolders);
        controller.putOrganToReceive(receiving, "ABC1234");
    }

}
