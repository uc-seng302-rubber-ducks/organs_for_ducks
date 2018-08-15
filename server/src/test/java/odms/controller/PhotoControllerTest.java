package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.utils.PhotoHelper;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhotoControllerTest {
    private PhotoController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private SocketHandler socketHandler;
    private User testUser;
    private Clinician testClinician;
    private static final String JPG_PHOTO_TEST_FILE_PATH = "src/test/resources/images/duck_jpg.jpg";
    private static final String PNG_PHOTO_TEST_FILE_PATH = "src/test/resources/images/duck_png.png";
    private static final String NOT_PHOTO_TEST_FILE_PATH = "src/test/resources/images/not_image.txt";
    private static final String THREE_MB_PHOTO_TEST_FILE_PATH = "src/test/resources/images/3mb_image.jpg";

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        socketHandler = mock(SocketHandler.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new PhotoController(manager, socketHandler);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
        testClinician = new Clinician("steve", "12", "password");
    }

    @Test
    public void putUserProfilePhotoJpgShouldReturnCreated() throws SQLException, IOException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);


        byte[] multipartFile = PhotoHelper.getBytesFromImage(JPG_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putUserProfilePhoto("ABC1234", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhotoPngShouldReturnCreated() throws SQLException, IOException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        byte[] multipartFile = PhotoHelper.getBytesFromImage(PNG_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putUserProfilePhoto("ABC1234", multipartFile,"image/png");
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhotoUserShouldReturnNotFoundWhenNoUserFound() throws SQLException, IOException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(null);

        byte[] multipartFile = PhotoHelper.getBytesFromImage(JPG_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putUserProfilePhoto("ZZZ1111", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhotoInvalidFileShouldReturnBadRequest() throws SQLException, IOException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        byte[] multipartFile = PhotoHelper.getBytesFromImage(NOT_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putUserProfilePhoto("ABC1234", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhoto2MBFileTestShouldReturnBadRequest() throws SQLException, IOException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        byte[] multipartFile = PhotoHelper.getBytesFromImage(THREE_MB_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putUserProfilePhoto("ABC1234", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, res.getStatusCode());
    }

    @Test
    public void putClinicianProfilePhotoJpgShouldReturnCreated() throws SQLException, IOException {
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(testClinician);
        byte[] multipartFile = PhotoHelper.getBytesFromImage(JPG_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putClinicianProfilePhoto("12", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putClinicianProfilePhotoPngShouldReturnCreated() throws SQLException, IOException {
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(testClinician);
        byte[] multipartFile = PhotoHelper.getBytesFromImage(PNG_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putClinicianProfilePhoto("12", multipartFile,"image/png");
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putClinicianProfilePhotoClinicianShouldReturnNotFoundWhenNoClinicianFound() throws SQLException, IOException{
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(null);
        byte[] multipartFile = PhotoHelper.getBytesFromImage(JPG_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putClinicianProfilePhoto("787979", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void putClinicianProfilePhotoInvalidFileShouldReturnBadRequest() throws SQLException, IOException {
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(testClinician);
        byte[] multipartFile = PhotoHelper.getBytesFromImage(NOT_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putClinicianProfilePhoto("12", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void putClinicianProfilePhoto2MBFileTestShouldReturnBadRequest() throws SQLException, IOException {
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(testClinician);

        Path path = Paths.get(THREE_MB_PHOTO_TEST_FILE_PATH);
        byte[] multipartFile = PhotoHelper.getBytesFromImage(THREE_MB_PHOTO_TEST_FILE_PATH);
        ResponseEntity res = controller.putClinicianProfilePhoto("12", multipartFile,"image/jpeg");
        Assert.assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, res.getStatusCode());
    }
}
