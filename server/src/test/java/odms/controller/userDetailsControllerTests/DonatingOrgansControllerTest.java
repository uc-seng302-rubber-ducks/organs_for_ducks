package odms.controller.userDetailsControllerTests;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ExpiryReason;
import odms.controller.user.details.DonatingOrgansController;
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
import java.util.HashMap;
import java.util.Map;

import static odms.commons.model._enum.Organs.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DonatingOrgansControllerTest {
    private DonatingOrgansController controller;
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
        controller = new DonatingOrgansController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void postReceivingOrganShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Map<Organs, ExpiryReason> donating = new HashMap<>();
        donating.put(LIVER, null);
        donating.put(HEART, null);
        ResponseEntity res = controller.postDonatingOrgans("ABC1234", donating);
        Assert.assertTrue(testUser.getDonorDetails().getOrgans().contains(LIVER));
        Assert.assertTrue(testUser.getDonorDetails().getOrgans().contains(HEART));
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void postReceivingOrganShouldReturnNotFoundWhenNoUserExists() {
        Map<Organs, ExpiryReason> donating = new HashMap<>();
        donating.put(LIVER, null);
        donating.put(HEART, null);
        ResponseEntity res = controller.postDonatingOrgans("ABC1234", donating);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postReceivingOrganShouldThrowServerDBErrorWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        Map<Organs, ExpiryReason> donating = new HashMap<>();
        donating.put(LIVER, null);
        donating.put(HEART, null);
        controller.postDonatingOrgans("ABC1234", donating);
    }

    @Test
    public void putReceivingOrgansShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        testUser.getDonorDetails().addOrgan(CORNEA, null);
        testUser.getDonorDetails().addOrgan(BONE_MARROW, null);
        Map<Organs, ExpiryReason> donating = new HashMap<>();
        donating.put(LIVER, null);
        donating.put(HEART, null);
        ResponseEntity res = controller.putDonatingOrgans("ABC1234", donating);
        Assert.assertTrue(testUser.getDonorDetails().getOrgans().contains(LIVER));
        Assert.assertTrue(testUser.getDonorDetails().getOrgans().contains(HEART));
        Assert.assertTrue(testUser.getDonorDetails().getOrgans().size() == 2);
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putReceivingOrgansShouldReturnNotFoundWhenNoUser() {
        Map<Organs, ExpiryReason> donating = new HashMap<>();
        donating.put(LIVER, null);
        donating.put(HEART, null);
        ResponseEntity res = controller.putDonatingOrgans("ABC1234", donating);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putReceivingOrgansShouldThrowExceptionAndReturn500() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        Map<Organs, ExpiryReason> donating = new HashMap<>();
        donating.put(LIVER, null);
        donating.put(HEART, null);
        controller.putDonatingOrgans("ABC1234", donating);
    }
}
